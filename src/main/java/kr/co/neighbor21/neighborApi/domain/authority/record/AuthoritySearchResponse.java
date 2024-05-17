package kr.co.neighbor21.neighborApi.domain.authority.record;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthoritySearchResponse(
        @Schema(description = "id / VARCHAR2(50)", example = "1")
        String id,
        @Schema(description = "authorityId / VARCHAR2(50)", example = "AUTH000001")
        String authorityId,
        @Schema(description = "authorityCode / VARCHAR2(50)", example = "POST:/v1/operator/search")
        String authorityCode,
        @Schema(description = "authorityName / VARCHAR2(50)", example = "search operator")
        String authorityName,
        @Schema(description = "description / VARCHAR2(100)", example = "search operator authority")
        String description) {
}

