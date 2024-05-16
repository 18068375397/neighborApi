package kr.co.neighbor21.neighborApi.domain.role.record;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;
import kr.co.neighbor21.neighborApi.entity.M_OP_AUTHORITY;

import java.util.List;


@Schema(description = "role")
public record RoleCreateRequest(
        @Schema(description = "name / VARCHAR2(256)", example = "admin")
        @ByteSize(max = 50)
        String name,
        @Schema(description = "authorities / List", example = "List")
        List<M_OP_AUTHORITY> authorities) {
}
