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
        @Schema(description = "운영자 ID / VARCHAR2(50)", example = "operator01")
        String userId,
        @Schema(description = "운영자 명 / VARCHAR2(256)", example = "운영자2")
        String userName,
        @Schema(description = "권한명 / VARCHAR(50)", example = "사용자 권한")
        String authorityName,
        @Schema(description = "운영자 전화번호 / VARCHAR2(256)", example = "02-1234-5678")
        String telephone,
        @Schema(description = "운영자 휴대전화 / VARCHAR2(256)", example = "010-1234-5678")
        String cellphone,
        @Schema(description = "운영자 직책 / VARCHAR2(30)", example = "연구원")
        String position,
        @Schema(description = "운영자 부서 / VARCHAR2(256)", example = "Meta 기술 개발 센터")
        String department,
        @Schema(description = "수정 일시 / DATE", example = "2024-01-01 00:00:00")
        String modifyDate,
        @Schema(description = "사용 여부 / BOOLEAN", example = "false")
        Boolean isUse) {
}
