package kr.co.neighbor21.neighborApi.domain.authority.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemResponse;
import kr.co.neighbor21.neighborApi.config.jwt.record.TokenResponse;
import kr.co.neighbor21.neighborApi.domain.authority.login.record.LoginRequest;
import kr.co.neighbor21.neighborApi.domain.authority.login.record.LogoutResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GEONLEE
 * @since 2023-06-05<br />
 * 2023-07-28 BITNA - 로그인 이력 추가<br />
 * 2023-08-01 BITNA - 로그인 이력 추가 시 date 저장<br />
 * 2023-10-04 GEONLEE - 로그인 성공 시 IP 정보 추가 및 패스워드 변경주기 관련 로직 수정<br />
 * 2023-10-23 MIJIN - 로그인 이력 추가 시 SystemCd 추가<br />
 * 2023-11-20 GEONLEE - 로그인 성공 응답 구조체 ResVO로 변경<br />
 * 2024-03-18 GEONLEE - @DefaultApiResponses 추가<br />
 * 2024-03-29 GEONLEE - 로그아웃 서비스 추가<br />
 **/
@RestController
@Tag(name = "[ API-000 ] Operator Login / Logout", description = "[담당자 : GEONLEE]")
@RequiredArgsConstructor
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    /**
     * 로그인 처리 Method
     *
     * @author GEONLEE
     * @since 2023-06-05<br />
     */
    @PostMapping(value = "/v1/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login", description = """
            ※id/pw If the pass-through validation is successful, the Access Token is sent to the cookie.
            ※Password Send data encrypted using RSA public key.
            
            Cookie After successful login, you can request the operation.
            Access Token Refresh Token is only sent to Authorization when it expires.
            """,
            operationId = "API-000-01"
    )
    public ResponseEntity<ItemResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest parameter
            , HttpServletResponse httpServletResponse) {
        return loginService.login(parameter, httpServletResponse);
    }

    /**
     * 로그인 처리 Method
     *
     * @author GEONLEE
     * @since 2023-06-05<br />
     */
    @PostMapping(value = "/v1/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Logout", description = """
             No parameters
            """,
            operationId = "API-000-02"
    )
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return loginService.logout(httpServletRequest, httpServletResponse);
    }
}
