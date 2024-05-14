package kr.co.neighbor21.neighborApi.domain.operator.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;

/**
 * 운영자 조회 요청
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Schema(description = "운영자 조회 요청")
public record OperatorSearchRequest(
        @Schema(description = "운영자 ID / VARCHAR2(50)", example = "geonlee")
        @NotNull
        @ByteSize(max = 50)
        String userId,
        @Schema(description = "운영자 명 / VARCHAR2(256)", example = "이건")
        @ByteSize(max = 256)
        String userName) {
}
