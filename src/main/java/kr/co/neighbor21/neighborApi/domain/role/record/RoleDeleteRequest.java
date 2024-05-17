package kr.co.neighbor21.neighborApi.domain.role.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;

import java.math.BigInteger;


@Schema(description = "role")
public record RoleDeleteRequest(
        @Schema(description = "id / int(50)", example = "1")
        @NotNull
        BigInteger id,
        @Schema(description = "roleId / VARCHAR2(50)", example = "ROLE001")
        @ByteSize(max = 50)
        String roleId) {
}
