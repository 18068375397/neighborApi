package kr.co.neighbor21.neighborApi.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.neighbor21.neighborApi.common.util.CommonUtils;
import kr.co.neighbor21.neighborApi.common.variable.CommonVariables;
import kr.co.neighbor21.neighborApi.config.jwt.record.TokenResponse;
import kr.co.neighbor21.neighborApi.config.rsa.RsaKeyGenerator;
import kr.co.neighbor21.neighborApi.domain.operator.record.OperatorSearchResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Token 의 생성, 인증정보 조회, 유효성 검증 등의 역할을 하는 클래스<br />
 * InitializingBean 을 implements 받는 이유는<br />
 * TokenProvider bean 이 생성이 되고, 주입을 받은 후에 secret 값을 Base64 Decode 해서<br />
 * key 변수에 할당 하기 위함.<br />
 *
 * @author GEONLEE
 * @since 2022-11-11<br />
 * 2023-07-21 BITNA getAuthId 추가<br />
 * 2023-11-17 GEONLEE - refresh token 을 header 가 아닌 claim 에 저장 해서 사용하는 방식으로 변경, resolveToken 에 null string 조건 추가<br />
 * 2023-12-04 GEONLEE - createToken overload 메서드로 변경<br />
 * 2024-03-28 GEONLEE - createToken, Access Token Claim 에 Refresh Token 저장하던 방식에서 따로 생성하도록 원복<br />
 * 2024-03-29 GEONLEE - getTokenFromCookie, renewalAccessTokenInCookie, expirationToken 메서드 구현<br />
 */
@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String JWT_COOKIE_NAME = "NS_AUT";
    public static final String AUTHORIZATION_FAIL_TYPE = "NS_AUT_FT";
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);
    private static final String TOKEN_TYPE = "Bearer";
    private static final String REFRESH_TOKEN = "refresh";
    /*Access token 만료 기한 : 1시간*/
    private static final long tokenValidityInMilliseconds = 1000 * 3600;
    /*Refresh token 만료 기한 : 일주일*/
    private static final long refreshTokenValidityInMilliseconds = 1000 * 604800;
    private final RsaKeyGenerator keyGen;

    @Value("${jwt.auth-key}")
    private String authoritiesKey;
    private Key signingKey;

    /**
     * Properties setting 후 처리<br />
     * 키값 설정
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.signingKey = keyGen.getPrivateKey();
    }

    /**
     * Access token 만 생성
     *
     * @author GEONLEE
     * @since 2024-03-28<br />
     */
    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliseconds);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(authoritiesKey, authorities)
                .signWith(this.signingKey, SignatureAlgorithm.RS256)
                .setExpiration(validity)
                .compact();
    }

    /**
     * token 생성 algorithm
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public TokenResponse createToken(Authentication authentication, Boolean isChangePassword) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliseconds);
        Date refreshValidity = new Date(now + refreshTokenValidityInMilliseconds);
        return TokenResponse.builder()
                .token(
                        Jwts.builder()
                                .setSubject(authentication.getName())
                                .claim(authoritiesKey, authorities)
                                .signWith(this.signingKey, SignatureAlgorithm.RS256)
                                .setExpiration(validity)
                                .compact())
                .refreshToken(
                        Jwts.builder()
                                .setSubject(authentication.getName())
                                .claim(authoritiesKey, authorities)
                                .signWith(this.signingKey, SignatureAlgorithm.RS256)
                                .setExpiration(refreshValidity)
                                .compact())
                .tokenType(TOKEN_TYPE)
                .isChangePassword(isChangePassword)
                .expirationSeconds(tokenValidityInMilliseconds)
                .build();
    }

    /**
     * token 생성 algorithm 간편인증 (ID만 받아서 토큰 생성)
     *
     * @author GEONLEE
     * @since 2023-05-21<br />
     */
    @Deprecated
    public TokenResponse createToken(OperatorSearchResponse operator) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliseconds);
        Date refreshValidity = new Date(now + refreshTokenValidityInMilliseconds);

        return TokenResponse.builder()
                .token(Jwts.builder()
                        .setSubject(operator.userId())
                        .claim(authoritiesKey, "ROLE_ADMIN")
                        .claim("refresh", Jwts.builder()
                                .setSubject(operator.userId())
                                .signWith(this.signingKey, SignatureAlgorithm.RS256)
                                .setIssuedAt(new Date(now))
                                .setExpiration(refreshValidity)
                                .compact())
                        .signWith(this.signingKey, SignatureAlgorithm.RS256)
                        .setIssuedAt(new Date(now))
                        .setExpiration(validity)
                        .compact())
                .tokenType(TOKEN_TYPE)
                .expirationSeconds(tokenValidityInMilliseconds)
                .build();
    }

    /**
     * 인증 정보 조회
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(authoritiesKey).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * token 에서 claim 추출
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(this.signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) { // Access Token
            return e.getClaims();
        }
    }

    /**
     * token 유효성 검증
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.signingKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            LOGGER.info("Invalid jwt signature.");
        } catch (ExpiredJwtException e) {
            LOGGER.error("Access token is expired.");
        } catch (UnsupportedJwtException e) {
            LOGGER.info("This jwt token is not supported.");
        } catch (IllegalArgumentException e) {
            LOGGER.info("Invalid jwt token.");
        } catch (DecodingException e) {
            LOGGER.info("JWT token decoding failed");
        }
        return false;
    }

    /**
     * token 으로 user 조회
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public String getUid(String token) throws MalformedJwtException, ExpiredJwtException {
        return Jwts.parserBuilder().setSigningKey(this.signingKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * token 으로 final login date 조회
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public Long getCreateTimestamp(String token) throws MalformedJwtException, ExpiredJwtException {
        return Long.valueOf(String.valueOf(
                Jwts.parserBuilder().setSigningKey(this.signingKey).build().parseClaimsJws(token).getBody().get("loginTime")));
    }

    /**
     * HttpServletRequest 에서 userId 추출
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public String getUserId(HttpServletRequest req) {
        try {
            String bearer = req.getHeader("Authorization");
            if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer")) {
                return getUid(bearer.substring(7));
            }
        } catch (MalformedJwtException | SignatureException e) {
            LOGGER.error("Weird refresh token.", e);
            throw new MalformedJwtException(e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("Access expired token. ===> logout");
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
        }
        return null;
    }

    /**
     * 권한 추출
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public String getAuthId(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(this.signingKey).build().parseClaimsJws(token).getBody().get(authoritiesKey);
    }

    /**
     * HttpServletRequest 에서 토큰 정보 추출
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    public String getTokenFromRequest(HttpServletRequest request) throws NullPointerException {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        String token = bearerToken.substring(7);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_TYPE + " ") && !"null".equals(token)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Cookie 에서 AccessToken 추출
     *
     * @author GEONLEE
     * @since 2024-03-28<br />
     */
    public String getTokenFromCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        String requestURI = httpServletRequest.getRequestURI().replace(CommonVariables.CONTEXT_PATH, "");
        if (cookies != null) {
            Optional<String> optionalAccessToken = Arrays.stream(cookies)
                    .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst();
            if (optionalAccessToken.isPresent()) {
                return doXssFilter(optionalAccessToken.get());
            }
        }
        LOGGER.error("Access token in cookie does not exist. request URI: {}", requestURI);
        return null;
    }

    /**
     * 쿠키에 Access Token 을 새로 생성한 token 으로 갱신한다.
     *
     * @author GEONLEE
     * @since 2023-03-28
     */
    public void renewalAccessTokenInCookie(HttpServletResponse httpServletResponse, String newAccessToken) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, newAccessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    /**
     * Cookie 의 토큰을 만료시킨다.
     *
     * @author GEONLEE
     * @since 2024-03-29<br />
     */
    public void expirationToken(HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath(CommonUtils.getPropertyValue("/"));
        httpServletResponse.addCookie(cookie);
    }

    /**
     * 크로스 스크립팅 방지
     *
     * @author GEONLEE
     * @since 2023-05-09<br />
     */
    private String doXssFilter(String origin) {
        return origin.replaceAll("'", "&#x27;").replaceAll("\"", "&quot;").replaceAll("\\(", "&#40;")
                .replaceAll("\\)", "&#41;").replaceAll("/", "&#x2F;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;").replaceAll("&", "&amp;");
    }

    /**
     * http header 에 token, refreshToken 을 담아주는 메소드
     * Deprecated 이유 -> 성공 시 body 에 token 정보를 주는데 헤더에 넣어줄 필요가 없다고 판단.
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     */
    @Deprecated
    public HttpHeaders setHeader(TokenResponse tokenDTO, String ts) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, "Bearer " + tokenDTO.token());
        headers.add("Login-Time", ts);
        return headers;
    }

    /**
     * claim 에서 refresh token 추출
     *
     * @author GEONLEE
     * @since 2024-02-13<br />
     */
    @Deprecated
    public String getRefreshToken(String token) throws NullPointerException {
        return getClaims(token).get(REFRESH_TOKEN).toString()
                .replaceAll("\r", "").replaceAll("\n", "");
    }
}