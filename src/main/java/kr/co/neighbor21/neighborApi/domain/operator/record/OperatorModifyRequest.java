package kr.co.neighbor21.neighborApi.domain.operator.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;

/**
 * 운영자 수정 요청
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Schema(description = "운영자 수정 요청")
public record OperatorModifyRequest(
        @Schema(description = "userId / VARCHAR2(50)", example = "test")
        @NotNull
        @ByteSize(max = 50)
        String userId,
        @Schema(description = "userName / VARCHAR2(256)", example = "geonlee")
        @ByteSize(max = 256)
        String userName,
        @Schema(description = "telephone / VARCHAR2(256)", example = "02-1234-5678")
        @ByteSize(max = 256)
        String telephone,
        @Schema(description = "cellphone / VARCHAR2(256)", example = "010-1234-5678")
        @ByteSize(max = 256)
        String cellphone,
        @Schema(description = "position / VARCHAR2(30)", example = "Researcher")
        @ByteSize(max = 30)
        String position,
        @Schema(description = "department / VARCHAR2(256)", example = "Meta Technology Development Center")
        @ByteSize(max = 256)
        String department,
        @Schema(description = "isUse / BOOLEAN", example = "false")
        Boolean isUse) {

}
