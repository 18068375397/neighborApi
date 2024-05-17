package kr.co.neighbor21.neighborApi.domain.operator.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.neighbor21.neighborApi.common.util.date.AljjabaegiDate;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;
import kr.co.neighbor21.neighborApi.entity.ROLE;

import java.util.List;

/**
 * 운영자 추가 요청
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@Schema(description = "운영자 추가 요청")
public record OperatorCreateRequest(
        @Schema(description = "userId / VARCHAR2(50)", example = "test")
        @NotNull
        @ByteSize(max = 50)
        String userId,
        @Schema(description = "password / VARCHAR2(256)", hidden = true)
        String password,
        @Schema(description = "userName / VARCHAR2(256)", example = "geonlee")
        @ByteSize(max = 256)
        String userName,
        @Schema(description = "telephone / VARCHAR2(256)", example = "02-1234-5678")
        @ByteSize(max = 256)
        String telephone,
        @Schema(description = "cellphone / VARCHAR2(256)", example = "010-1234-5678")
        @ByteSize(max = 256)
        String cellphone,
        @Schema(description = "position / VARCHAR2(30)", example = "연구원")
        @ByteSize(max = 30)
        String position,
        @Schema(description = "department / VARCHAR2(256)", example = "Meta 기술 개발 센터")
        @ByteSize(max = 256)
        String department,
        @Schema(description = "modifyDate / DATE", hidden = true)
        String modifyDate,
        @Schema(description = "enable or not / BOOLEAN", example = "true")
        Boolean isUse,
        @Schema(description = "Password update date / DATE", hidden = true)
        String passwordUpdateDate,
        @Schema(description = "Password modification cycle / NUMBER(4)", hidden = true)
        Long passwordUpdateCycle,
        @Schema(description = "roles / List", example = "List")
        List<ROLE> roles) {

    public OperatorCreateRequest {
        modifyDate = AljjabaegiDate.getCurrentDateTimeString();
        password = "$2a$10$5BjKHQv6KGDA5h1N3QKYq.FPtfqegzfcIOFHw8FXEPkrltGVcdk7S";
        passwordUpdateDate = modifyDate;
        passwordUpdateCycle = 90L;
    }
}
