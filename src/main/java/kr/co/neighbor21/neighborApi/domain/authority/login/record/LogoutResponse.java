package kr.co.neighbor21.neighborApi.domain.authority.login.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 로그아웃 응답 구조체
 *
 * @author GEONLEE
 * @since 2024-03-29
 */
@Builder
@Schema(description = "로그아웃 응답")
public record LogoutResponse(
        @Schema(description = "Response code", example = "NS_OK")
        String status,
        @Schema(description = "Response message", example = "I signed off.")
        String message) {
}
