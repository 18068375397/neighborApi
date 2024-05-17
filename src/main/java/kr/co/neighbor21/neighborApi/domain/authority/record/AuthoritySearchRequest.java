package kr.co.neighbor21.neighborApi.domain.authority.record;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;

@Schema(description = "authority")
public record AuthoritySearchRequest(
        @Schema(description = "authorityId / VARCHAR2(50)", example = "AUTH000001")
        @ByteSize(max = 50)
        String authorityId,
        @Schema(description = "authorityName / VARCHAR2(50)", example = "search operator")
        @ByteSize(max = 50)
        String authorityName){
}
