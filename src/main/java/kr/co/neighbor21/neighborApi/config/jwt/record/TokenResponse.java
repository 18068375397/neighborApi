package kr.co.neighbor21.neighborApi.config.jwt.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Token DTO - API 사용자에게 전달되는 Response token 구조체
 *
 * @author GEONLEE
 * @since 2022-11-11<br />
 * 2022-11-15 - GEONLEE Builder 패턴으로 변경<br />
 * 2023-01-30 GEONLEE - tokenType(토큰타입), expireSec(만료시간(초)) 추가<br />
 * 2023-03-16 GEONLEE - 사용자 상태코드, 상태코드, 상태메시지, 사용자 ID 추가<br />
 * 2023-11-17 GEONLEE - refresh token 제거 (token claim 값에 추가하는 것으로 변경)<br />
 * 2024-03-06 GEONLEE - record 로 변경<br />
 * 2024-03-19 GEONLEE - 불필요 속성 제거<br />
 */
@Schema(description = "JWT Token 응답 DTO")
@Builder
public record TokenResponse(
        @Schema(description = "Access Token", example = "eyJhbGciOiJSUzI1NiJ9...", hidden = true)
        @JsonIgnore
        String token,
        @Schema(description = "Refresh Token", example = "eyJhbGciOiJSUzI1NiJ9...")
        String refreshToken,
        @Schema(description = "Token 타입", example = "Bearer")
        String tokenType,
        @Schema(description = "Token 만료 시간 (초)", example = "600000")
        Long expirationSeconds,
        @Schema(description = "비밀번호 갱신 여부 (갱신주기가 지난 경우 true 아니면 false)")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        boolean isChangePassword) {
}