package kr.co.neighbor21.neighborApi.domain.role.record;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.neighbor21.neighborApi.entity.M_OP_AUTHORITY;

import java.util.List;


@Schema(description = "role")
public record RoleModifyResponse(
        @Schema(description = "id / VARCHAR2(50)", example = "1")
        String id,
        @Schema(description = "name / VARCHAR2(256)", example = "admin")
        String name,
        @Schema(description = "authorities / List", example = "List")
        List<M_OP_AUTHORITY> authorities) {
}
