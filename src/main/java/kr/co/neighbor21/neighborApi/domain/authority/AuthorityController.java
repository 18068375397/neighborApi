package kr.co.neighbor21.neighborApi.domain.authority;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemsResponse;
import kr.co.neighbor21.neighborApi.domain.authority.record.AuthoritySearchRequest;
import kr.co.neighbor21.neighborApi.domain.authority.record.AuthoritySearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "[ API-003 ] authority manager")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @PostMapping(value = "/v1/authority/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "View authority information ( how to use SearchRequest )", description = """
            # Parameters
            - authorityName [authority Name]
            """,
            operationId = "API-003-01"
    )
    public ResponseEntity<ItemsResponse<AuthoritySearchResponse>> getOperatorList(@RequestBody @Valid AuthoritySearchRequest parameter) {
        return authorityService.getAuthorityList(parameter);
    }
}
