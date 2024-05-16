package kr.co.neighbor21.neighborApi.domain.role.record;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;


@Schema(description = "role")
public record RoleSearchRequest(
        @Schema(description = "name / VARCHAR2(256)", example = "admin")
        @ByteSize(max = 50)
        String name) {
}
