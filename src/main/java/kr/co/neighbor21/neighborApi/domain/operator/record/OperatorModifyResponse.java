package kr.co.neighbor21.neighborApi.domain.operator.record;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 운영자 수정 응답
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Schema(description = "운영자 수정 응답")
public record OperatorModifyResponse(
        @Schema(description = "userId / VARCHAR2(50)", example = "test")
        String userId,
        @Schema(description = "userName / VARCHAR2(256)", example = "geonlee")
        String userName,
        @Schema(description = "telephone / VARCHAR2(256)", example = "02-1234-5678")
        String telephone,
        @Schema(description = "cellphone / VARCHAR2(256)", example = "010-1234-5678")
        String cellphone,
        @Schema(description = "position / VARCHAR2(30)", example = "Researcher")
        String position,
        @Schema(description = "department / VARCHAR2(256)", example = "Meta Technology Development Center")
        String department,
        @Schema(description = "modifyDate / DATE", example = "2024-01-01 00:00:00")
        String modifyDate,
        @Schema(description = "isUse / VARCHAR2(1)", example = "true")
        Boolean isUse) {
}
