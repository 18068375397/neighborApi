package kr.co.neighbor21.neighborApi.domain.operator.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;

/**
 * 운영자 삭제 요청
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Schema(description = "운영자 삭제 요청")
public record OperatorDeleteRequest(
        @Schema(description = "userId / VARCHAR2(50)", example = "test")
        @NotNull
        @ByteSize(max = 50)
        String userId) {
}
