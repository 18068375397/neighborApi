package kr.co.neighbor21.neighborApi.config.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import static kr.co.neighbor21.neighborApi.config.jwt.TokenProvider.AUTHORIZATION_FAIL_TYPE;

/**
 * 401 UNAUTHORIZED (인증 실패) 처리용 응답 클래스
 *
 * @author GEONLEE
 * @since 2022-11-11<br />
 * 2023-07-21 BITNA 권한별 menuUrl 접근 처리<br />
 * 2023-11-20 GEONLEE 중복 로그인 처리 방식 변경<br />
 * 기존에 duplicationLogin 값을 header 에서 추출하는 방식에서 중복로그인 코드로 처리하도록 변경, Access token 없을 때 처리 추가<br />
 * 2023-11-22 GEONLEE RTR(Refresh Token Rotation) 갱신 1번만 가능하도록 적용<br />
 * 2023-12-04 GEONLEE setResponseWriter - Response writer 메서드 추가, 불필요 코드 제거 및 로직 정리<br />
 * 2024-03-15 GEONLEE - setResponseWriter 메서드 CommonUtils 로 이동<br />
 * 2024-03-26 GEONLEE - token 만료 시 Operator 의 권한에 접근하기 위해 @Transactional 추가<br />
 * 2024-03-28 GEONLEE - doXssFilter JwtFilter 로 이동, 기존 기능을 JwtFilter 에서 처리하고 응답만 리턴하도록 로직 정리<br />
 * 2024-03-29 GEONLEE - 중복 로그인 응답 처리 구분<br />
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        CommonErrorCode errorCode = (CommonErrorCode) request.getSession().getAttribute(AUTHORIZATION_FAIL_TYPE);
        if (errorCode != CommonErrorCode.DUPLICATION_LOGIN) {
            errorCode = CommonErrorCode.NOT_AUTHENTICATION;
        }
        CommonUtils.setResponseWriter(response, errorCode.getResultCode(), errorCode.getResultMsg());
    }
}