package kr.co.neighbor21.neighborApi.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.neighbor21.neighborApi.common.contextHolder.ApplicationContextHolder;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.variable.CommonVariables;
import kr.co.neighbor21.neighborApi.config.jwt.record.JwtValidDto;
import kr.co.neighbor21.neighborApi.domain.operator.OperatorRepository;
import kr.co.neighbor21.neighborApi.entity.M_OP_OPERATOR;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static kr.co.neighbor21.neighborApi.config.jwt.TokenProvider.AUTHORIZATION_FAIL_TYPE;

/**
 * JWT Filter
 * Cookie 에서 Access Token 을 추출해 유효성 검증 및 중복 로그인 여부를 체크
 *
 * @author GEONLEE
 * @since 2022-11-11<br />
 * 2023-07-21 BITNA 권한별 menuUrl 접근 처리(일단 주석)<br />
 * 2023-10-17 GEONLEE - swagger 3.x update 로 requestURI.startsWith("-docs") 추가<br />
 * 2023-11-20 GEONLEE - 중복 로그인 처리 프로세스 변경. header 는 건들지 않고 response status 코드로 처리. 불필요 코드 제거<br />
 * 2023-12-04 GEONLEE - 불필요 코드 제거 및 로직 정리, 주석 추가<br />
 * 2024-03-15 GEONLEE - Access Token 이 전달되지 않았을 경우 바로 에러 응답 리턴하도록 변경, Security filter 타지 않음<br />
 * - Access Token 의 형태가 올바르지 않을 경우 바로 에러 응답 리턴하도록 변경, Security filter 타지 않음<br />
 * 2024-03-28 GEONLEE - Filter 에서 바로 응답하지 않고 JwtAuthenticationEntryPoint 로 전달 하도록 로직 개선<br />
 * 2024-04-04 GEONLEE - 버그 수정, JwtValidDto 에서 token 추출, refresh token 유효성 검증 부분 
 */
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
    private final List<String> ignoreUris = List.of(CommonVariables.IGNORE_URIS);
    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = httpServletRequest.getRequestURI().replace(CommonVariables.CONTEXT_PATH, "");
        if (!ignoreUris.contains(requestURI) && !requestURI.startsWith("/swagger-") && !requestURI.startsWith("/api-docs")) {
            LOGGER.info("Request URI : '{}', Start to check access token. ▼", requestURI);
            String accessToken;
            /*1. Cookie Extract the access token from (NS_AUT) */
            accessToken = tokenProvider.getTokenFromCookie(httpServletRequest);
            JwtValidDto valid = new JwtValidDto(false, null, accessToken);
            /*2. Access Token Verification check*/
            if (StringUtils.hasText(accessToken)) {
                checkTokenValidity(valid, httpServletRequest, httpServletResponse);
            }
            /*3. Check whether it is a duplicate login*/
            if (valid.isValid()) checkDuplicationLogin(valid, httpServletRequest);
            /*4. Spring security Save permission information to
             * If there is no permission information, it is forwarded to JwtAuthenticationEntryPoint. (set in security config)*/
            if (valid.isValid()) {
                LOGGER.info("User's token validation success: '{}'", valid.getUserId());
                Authentication authentication = tokenProvider.getAuthentication(valid.getAccessToken());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * check Token Validity
     *
     * @author GEONLEE
     * @since 2024-03-28
     */
    private void checkTokenValidity(JwtValidDto valid,
                                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (!StringUtils.hasText(valid.getAccessToken()) || !tokenProvider.validateToken(valid.getAccessToken())) {
            valid.setValid(false);
            String refreshToken;
            /*Access Token If it expires, please check the RefreshToken*/
            try {
                refreshToken = tokenProvider.getTokenFromRequest(httpServletRequest);
                if (StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)) {
                    /*refreshToken Extract information to create a new Access Token*/
                    Authentication authentication = tokenProvider.getAuthentication(refreshToken);
                    String userId = tokenProvider.getUid(refreshToken);
                    valid.setUserId(userId);
                    String newAccessToken = tokenProvider.createAccessToken(authentication);
                    /*쿠키 Access Token Update information*/
                    tokenProvider.renewalAccessTokenInCookie(httpServletResponse, newAccessToken);
                    /*Refresh Token Update Access Token by viewing users*/
                    OperatorRepository userRepository = ApplicationContextHolder.getContext().getBean(OperatorRepository.class);
                    M_OP_OPERATOR optionalEntity = userRepository.findOneByUserIdAndRefreshToken(valid.getUserId(), refreshToken);
                    if (optionalEntity != null) {
                        optionalEntity.setAccessToken(newAccessToken);
                        userRepository.save(optionalEntity);
                        valid.setAccessToken(newAccessToken);
                        valid.setValid(true);
                        LOGGER.info("Renew user's access token with refresh token: '{}'", valid.getUserId());
                    } else {
                        LOGGER.error("User's refresh token is different. (Duplicated login): '{}'", valid.getUserId());
                    }
                }
            } catch (NullPointerException e) {
                LOGGER.error("Refresh token extraction failed.");
            }
        } else {
            String userId = tokenProvider.getUid(valid.getAccessToken());
            valid.setValid(true);
            valid.setUserId(userId);
        }
    }

    /**
     * check Duplication Login<br />
     * DB의 Access Token 과 비교<br />
     *
     * @author GEONLEE
     * @since 2024-03-28
     */
    private void checkDuplicationLogin(JwtValidDto valid, HttpServletRequest httpServletRequest) {
        OperatorRepository userRepository = ApplicationContextHolder.getContext().getBean(OperatorRepository.class);
        M_OP_OPERATOR optionalEntity = userRepository.findOneByUserIdAndAccessToken(valid.getUserId(), valid.getAccessToken());
        if (optionalEntity == null) {
            LOGGER.error("User's access token is different. (Duplicated login): '{}'", valid.getUserId());
            valid.setValid(false);
            httpServletRequest.getSession().setAttribute(AUTHORIZATION_FAIL_TYPE, CommonErrorCode.DUPLICATION_LOGIN);
        }
    }
}