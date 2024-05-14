package kr.co.neighbor21.neighborApi.domain.authority.login;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.custom.ServiceException;
import kr.co.neighbor21.neighborApi.common.exception.custom.UnauthorizedException;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemResponse;
import kr.co.neighbor21.neighborApi.common.util.date.AljjabaegiDate;
import kr.co.neighbor21.neighborApi.config.jwt.TokenProvider;
import kr.co.neighbor21.neighborApi.config.jwt.record.TokenResponse;
import kr.co.neighbor21.neighborApi.config.message.MessageConfig;
import kr.co.neighbor21.neighborApi.config.rsa.RsaKeyGenerator;
import kr.co.neighbor21.neighborApi.domain.authority.login.record.LoginRequest;
import kr.co.neighbor21.neighborApi.domain.authority.login.record.LogoutResponse;
import kr.co.neighbor21.neighborApi.domain.operator.OperatorRepository;
import kr.co.neighbor21.neighborApi.entity.M_OP_OPERATOR;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 로그인 처리 Service
 *
 * @author GEONLEE
 * @since 2023-06-05<br />
 * 2024-03-28 GEONLEE - 로그인 인증 로직 개선 -> cookie 사용 및 Access Token DB 저장<br />
 * 2024-03-29 GEONLEE - 로그아웃 서비스 구현, CommonErrorCode 변경에 따른 처리<br />
 * 2024-03-30 GEONLEE - 코드 개선
 */
@Service
@RequiredArgsConstructor
public class LoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final OperatorRepository operatorRepository;
    private final RsaKeyGenerator keyGen;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    private final MessageConfig messageConfig;

    @Transactional
    public ResponseEntity<ItemResponse<TokenResponse>> login(
            LoginRequest parameter, HttpServletResponse httpServletResponse) {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMsg = messageConfig.getMsg("LOGIN.SUCCESS.MSG");
        /*1. ID가 Check to see if it exists*/
        M_OP_OPERATOR entity = operatorRepository.findOneByKeyUserId(parameter.id())
                .orElseThrow(() -> new UnauthorizedException(CommonErrorCode.NO_MATCHING_USER, null));
        /*2. Password Check if there is a match*/
        String decryptedPassword;
        decryptedPassword = keyGen.decryptRSA(parameter.password());
        if (decryptedPassword == null) throw new IllegalArgumentException("Failed to decrypt password encrypted with RSA.");
        if (!encoder.matches(decryptedPassword.trim(), entity.getPassword())) {
            throw new UnauthorizedException(CommonErrorCode.WRONG_PASSWORD, null);
        }
        LOGGER.info("Correct password: {}", parameter.id());
        Long passwordChangeCycle = 0L;
        boolean isChangePassword;
        /*3. Password Check the update cycle*/
        try {
            passwordChangeCycle = entity.getPasswordUpdateCycle();
            isChangePassword = AljjabaegiDate.getCurrentLocalDate()
                    .isAfter(entity.getPasswordUpdateDate().toLocalDate().plusDays(passwordChangeCycle));
        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.info(
                    "Please update '{}' password change cycle. -> password update date: {}, password update cycle: {}",
                    parameter.id(), entity.getPasswordUpdateDate(), passwordChangeCycle);
            isChangePassword = true;
        }
        /*4. Check user permissions*/
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                parameter.id(), decryptedPassword);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        /*5. JWT Create token*/
        TokenResponse tokenResponse = tokenProvider.createToken(authentication, isChangePassword);
        /*6. Update DB Token information when the login is successful */
        entity.setAccessToken(tokenResponse.token());
        entity.setRefreshToken(tokenResponse.refreshToken());
        /*7. Add Access Token to Cookie*/
        tokenProvider.renewalAccessTokenInCookie(httpServletResponse, tokenResponse.token());

//        /*Add login IP information*/
//        String ip = getClientIpAddress(request);
//        LOGGER.info("Login {} ip: {}", parameter.id(), ip);
        /* Add login history */
//        LoginLogDTO loginLogDTO = new LoginLogDTO();
//        loginLogDTO.setIp(getClientIpAddress(httpServletRequest));
//        loginLogDTO.setRegDt(AljjabaegiDate.getCurrentDateTimeString());
//        loginLogDTO.setUserId(parameter.getLoginId());
//        loginLogDTO.setLoginDiv("01");
//        loginLogDTO.setSystemCd("01");
//
//        loginLogService.insLoginRecord(loginLogDTO);

        return ResponseEntity.ok()
                .body(ItemResponse.<TokenResponse>builder()
                        .status(resultCode)
                        .message(resultMsg)
                        .item(tokenResponse)
                        .build());
    }

    @Transactional
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        String resultCode = messageConfig.getCode("SUCCESS.CODE");
        String resultMessage = messageConfig.getMsg("LOGOUT.SUCCESS.MSG");
        String accessToken = tokenProvider.getTokenFromCookie(request);
        if (StringUtils.hasText(accessToken)) {
            try {
                String userId = tokenProvider.getUid(accessToken);
                tokenProvider.expirationToken(response);
                Optional<M_OP_OPERATOR> optionalEntity = operatorRepository.findOneByKeyUserId(userId);
                if (optionalEntity.isPresent()) {
                    optionalEntity.get().setAccessToken(null);
                    optionalEntity.get().setRefreshToken(null);
                }
            } catch (JwtException e) {
                throw new UnauthorizedException(CommonErrorCode.EXPIRED_TOKEN, e);
            } catch (PersistenceException e) {
                throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
            }
        } else {
            throw new UnauthorizedException(CommonErrorCode.NOT_AUTHENTICATION, null);
        }

        return ResponseEntity.ok()
                .body(LogoutResponse.builder()
                        .status(resultCode)
                        .message(resultMessage)
                        .build());
    }

    /**
     * request 에서 ip 정보 추출
     *
     * @author GEONLEE
     * @since 2023-06-05<br />
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("REMOTE_ADDR");
        }

        ipAddress = ipAddress.replaceAll("0:0:0:0:0:0:0:1", "127.0.0.1");

        return ipAddress;
    }
}
