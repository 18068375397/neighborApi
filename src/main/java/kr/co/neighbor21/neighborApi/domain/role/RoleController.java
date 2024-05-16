package kr.co.neighbor21.neighborApi.domain.role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemsResponse;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleCreateRequest;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleCreateResponse;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleSearchRequest;
import kr.co.neighbor21.neighborApi.domain.role.record.RoleSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "[ API-002 ] role authority manager")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping(value = "/v1/role/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "View operator information ( how to use SearchRequest )", description = """
            # Parameters
            - name [role Name]
            """,
            operationId = "API-002-01"
    )
    public ResponseEntity<ItemsResponse<RoleSearchResponse>> getOperatorList(@RequestBody @Valid RoleSearchRequest parameter) {
        return roleService.getRoleList(parameter);
    }

    @PostMapping(value = "/v1/role/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "create role with authority", description = """
            # Parameters
            - name [role Name]
            - List [M_OP_AUTHORITY]
            """,
            operationId = "API-002-02"
    )
    public ResponseEntity<ItemsResponse<RoleCreateResponse>> create(@RequestBody @Valid RoleCreateRequest parameter) {
        return roleService.createRole(parameter);
    }

}
