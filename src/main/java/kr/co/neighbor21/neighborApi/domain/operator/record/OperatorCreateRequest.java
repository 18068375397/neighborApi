package kr.co.neighbor21.neighborApi.domain.operator.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.neighbor21.neighborApi.common.util.date.AljjabaegiDate;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;

/**
 * 운영자 추가 요청
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Schema(description = "운영자 추가 요청")
public record OperatorCreateRequest(
        @Schema(description = "운영자 ID / VARCHAR2(50)", example = "operator01")
        @NotNull
        @ByteSize(max = 50)
        String userId,
        @Schema(description = "운영자 비밀번호 / VARCHAR2(256)", hidden = true)
        String password,
        @Schema(description = "운영자 명 / VARCHAR2(256)", example = "운영자1")
        @ByteSize(max = 256)
        String userName,
        @Schema(description = "권한 ID / NUMBER(6)", example = "AUTH000002")
        String authorityId,
        @Schema(description = "운영자 전화번호 / VARCHAR2(256)", example = "02-1234-5678")
        @ByteSize(max = 256)
        String telephone,
        @Schema(description = "운영자 휴대전화 / VARCHAR2(256)", example = "010-1234-5678")
        @ByteSize(max = 256)
        String cellphone,
        @Schema(description = "운영자 직책 / VARCHAR2(30)", example = "연구원")
        @ByteSize(max = 30)
        String position,
        @Schema(description = "운영자 부서 / VARCHAR2(256)", example = "Meta 기술 개발 센터")
        @ByteSize(max = 256)
        String department,
        @Schema(description = "수정 일시 / DATE", hidden = true)
        String modifyDate,
        @Schema(description = "사용 여부 / BOOLEAN", example = "true")
        Boolean isUse,
        @Schema(description = "패스워드갱신 일자 / DATE", hidden = true)
        String passwordUpdateDate,
        @Schema(description = "패스워드수정주기 / NUMBER(4)", hidden = true)
        Long passwordUpdateCycle) {
    public OperatorCreateRequest {
        modifyDate = AljjabaegiDate.getCurrentDateTimeString();
        password = "$2a$10$5BjKHQv6KGDA5h1N3QKYq.FPtfqegzfcIOFHw8FXEPkrltGVcdk7S";
        passwordUpdateDate = modifyDate;
        passwordUpdateCycle = 90L;
    }
}
