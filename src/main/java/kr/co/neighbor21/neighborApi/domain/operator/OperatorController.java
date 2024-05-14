package kr.co.neighbor21.neighborApi.domain.operator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.neighbor21.neighborApi.common.request.DynamicSearchRequest;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemsResponse;
import kr.co.neighbor21.neighborApi.domain.operator.record.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 운영자 Controller
 *
 * @author GEONLEE
 * @since 2024-03-21<br />
 */
@RestController
@Tag(name = "[ API-001 ] View and edit operator information", description = "[담당자 : GEONLEE]")
@SecurityRequirement(name = "JWT Token")
@RequiredArgsConstructor
public class OperatorController {
    private final OperatorService operatorService;

    @PostMapping(value = "/v1/operator/dynamic-search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "View operator information (how DynamicSearchRequest is used)", description = """
            # Searchable Fields
            - userId [userId]
            - userName [userName]
            """,
            operationId = "API-001-01-1"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            examples = {
                    @ExampleObject(name = "DynamicSearchRequest", value = """
                                    {
                                             "take": 10,
                                             "skip": 0,
                                             "sort": [
                                                   {
                                                           "field": "modifyDate",
                                                           "dir": "desc"
                                                    }
                                             ],
                                             "filter": [
                                                   {
                                                           "field": "userName",
                                                           "operator": "contains",
                                                           "value": "이건"
                                                    }
                                             ]
                                     }
                            """)
            }
    ))
    public ResponseEntity<ItemsResponse<OperatorSearchResponse>> getOperatorListByDynamicRequest(@RequestBody DynamicSearchRequest param) {
        return operatorService.getOperatorListByDynamicRequest(param);
    }

    @PostMapping(value = "/v1/operator/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "View operator information ( how to use SearchRequest )", description = """
            # Parameters
            - userId [operator ID]<font color='red'>*</font>
            - userName [operator Name]
            """,
            operationId = "API-001-01-2"
    )
    public ResponseEntity<ItemsResponse<OperatorSearchResponse>> getOperatorList(@RequestBody @Valid OperatorSearchRequest parameter) {
        return operatorService.getOperatorList(parameter);
    }

    @PostMapping(value = "/v1/operator/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "운영자 정보 추가", operationId = "API-001-02")
    public ResponseEntity<ItemResponse<OperatorCreateResponse>> createOperator(@RequestBody @Valid OperatorCreateRequest parameter) {
        return operatorService.createOperator(parameter);
    }

    @PostMapping(value = "/v1/operator/modify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "운영자 정보 수정", operationId = "API-001-03")
    public ResponseEntity<ItemResponse<OperatorModifyResponse>> modifyOperator(@RequestBody @Valid OperatorModifyRequest parameter) {
        return operatorService.modifyOperator(parameter);
    }

    @PostMapping(value = "/v1/operator/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "운영자 정보 삭제", operationId = "API-001-04")
    public ResponseEntity<ItemResponse<Long>> deleteOperator(@RequestBody @Valid OperatorDeleteRequest parameter) {
        return operatorService.deleteOperator(parameter);
    }
}
