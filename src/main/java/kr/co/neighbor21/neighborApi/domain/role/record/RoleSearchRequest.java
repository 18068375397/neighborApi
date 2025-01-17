package kr.co.neighbor21.neighborApi.domain.role.record;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;


@Schema(description = "role")
public record RoleSearchRequest(
        @Schema(description = "roleId / VARCHAR2(50)", example = "ROLE001")
        @ByteSize(max = 50)
        String roleId,
        @Schema(description = "roleName / VARCHAR2(50)", example = "admin")
        @ByteSize(max = 50)
        String roleName) {
}
