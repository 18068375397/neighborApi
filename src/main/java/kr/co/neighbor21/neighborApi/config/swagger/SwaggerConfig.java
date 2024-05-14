package kr.co.neighbor21.neighborApi.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.code.ErrorCode;
import kr.co.neighbor21.neighborApi.common.response.structure.ErrorResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemResponse;
import kr.co.neighbor21.neighborApi.config.jwt.record.TokenResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WEB MVC 및 Swagger 관련 설정
 * 참조 <a href="https://swagger.io/docs/specification/authentication/">...</a>
 *
 * @author GEONLEE
 * @version 1.0.0
 * @since 2023-10-17<br />
 * 2023-12-04 GEONLEE - v1 Group 추가<br />
 * 2024-03-19 GEONLEE - OperationCustomizer 추가<br />
 * 2024-03-29 GEONLEE - 에러 코드 정리에 따른 수정사항 반영<br />
 */
@OpenAPIDefinition(
        info = @Info(title = "Neighbor System API Documentation",
                description = """
                        Neighbor System API Specification
                        - <a href="https://aljjabaegi.tistory.com/659" target="_blank">JWT</a>
                        - <a href="https://aljjabaegi.tistory.com/672" target="_blank">RSA</a>
                        - <a href="https://aljjabaegi.tistory.com/481" target="_blank">Lombok</a>
                        - Spring Security
                        - <a href="https://aljjabaegi.tistory.com/category/Programing/JPA" target="_blank">JPA (querydsl, mapstruct)</a>
                        - Logging (Information, Exception, Request, Response)
                                                
                        """, version = "v1.0"),
        servers = @Server(url = "/neighbor-api") //ip:port 까지 입력할 경우 CORS 발생
)
@SecurityScheme(
        name = "JWT Token",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class SwaggerConfig {
    public static final List<String> ignoreMethods = List.of("login", "getPublicKey");

    @Bean
    public GroupedOpenApi version1APi() {
        return GroupedOpenApi.builder()
                .group("v1.0")
                .pathsToMatch("/v1/**")
                .addOperationCustomizer(operationCustomizer())
                .build();
    }

    @Bean
    public GroupedOpenApi version2APi() {
        return GroupedOpenApi.builder().group("v2.0").pathsToMatch("/v2/**").build();
    }

    /**
     * API Operation Custom Method
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponses apiResponses = operation.getResponses();
            if (apiResponses == null) {
                apiResponses = new ApiResponses();
                operation.setResponses(apiResponses);
            }
            ApiResponse response = apiResponses.get("200");
            apiResponses.put("NS_OK", response);
            apiResponses.remove("200");
            apiResponses.remove("500");
            apiResponses.putAll(getDefaultResponses(handlerMethod));
            return operation;
        };
    }

    private Map<String, ApiResponse> getDefaultResponses(HandlerMethod handlerMethod) {
        LinkedHashMap<String, ApiResponse> responses = new LinkedHashMap<>();
        boolean isAuthentication = true;
        responses.put("NS_ERR_CT", clientError());
        if (ignoreMethods.contains(handlerMethod.getMethod().getName())) {
            isAuthentication = false;
        }
        responses.put("NS_ERR_AT", authenticationError(isAuthentication));
        responses.put("NS_ERR_FD", forbiddenError());
        responses.put("NS_ERR_SV", serverError());

        return responses;
    }

    /**
     * 400 - 클라이언트 요청 실패 관련 Response 리턴 메서드
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    private ApiResponse clientError() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("""
                Bad Request
                - Confirm whether the requested information is correct.
                
                Detailed code.
                - 01 : The URI you requested cannot be found.
                - 02 : The request method is not supported.
                - 03 : The type of content you requested is not supported.
                - 04 : Invalid parameter has been passed.
                - 05 : There are no required parameters.
                - 06 : Access to inaccessible information.
                - 07 : Cannot find the corresponding ID.
                - 08 : The ID that already exists.
                """);
        addContent(apiResponse, CommonErrorCode.URI_NOT_FOUND);
        return apiResponse;
    }

    /**
     * 403 - 권한 관련 Response 리턴 메서드
     *
     * @author GEONLEE
     * @since 2024-03-29<br />
     */
    private ApiResponse forbiddenError() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("""
                Forbidden
                - Check that the permissions are correct.
                """);
        addContent(apiResponse, CommonErrorCode.FORBIDDEN);
        return apiResponse;
    }

    /**
     * 500 - 서비스 예외 관련 Response 리턴 메서드
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    private ApiResponse serverError() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("""
                Internal Server Error (Unchecked Exception)
                - request error checking from the API representative.
                
                Detailed code.
                - 01: there is a problem with the service you requested.
                - 02: failed to process data.
                """);
        addContent(apiResponse, CommonErrorCode.SERVICE_ERROR);
        return apiResponse;
    }

    /**
     * 401 - 인증 실패 관련 Response 리턴 메서드
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    private ApiResponse authenticationError(boolean isAuthentication) {
        ApiResponse apiResponse = new ApiResponse();
        String description;
        if (isAuthentication) {
            description = """
                    Unauthorized
                    - check the information related to authentication.
                    - message "credentials failed."Same as.
                    
                    Detailed code.
                    
                    - 01: no authentication information.
                    - 02: log in repeatedly.
                    - 03: token expires
                    """;
        } else {
            description = """
                    Unauthorized
                    - check the information related to authentication.
                    - message "credentials failed."Same as.
                    
                    Detailed code.
                    
                    - 04: there are no matching users.
                    - 05: incorrect password
                    """;
        }
        apiResponse.setDescription(description);
        addContent(apiResponse, CommonErrorCode.NOT_AUTHENTICATION);
        return apiResponse;
    }

    /**
     * 404 - Not Found 관련 Response 리턴 메서드
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    @Deprecated
    private ApiResponse err404() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("""
                Not Found
                - Verify that the requested URI is correct.
                """);
        addContent(apiResponse, CommonErrorCode.URI_NOT_FOUND);
        return apiResponse;
    }

    /**
     * 405 - Method Not Allowed 관련 Response 리턴 메서드
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    @Deprecated
    private ApiResponse err415() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("""
                Method Not Allowed
                - API HTTP Method Please confirm that it is in accordance with the requirements.
                """);
        addContent(apiResponse, CommonErrorCode.UNSUPPORTED_MEDIA_TYPE);
        return apiResponse;
    }

    /**
     * 201 - Token 자동 생성 관련 Response 리턴 메서드
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     * 2024-03-29 JWT 로직 변경에 따른 @Deprecated 처리, Cookie update 로 변경<br />
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    private ApiResponse err201() {
        ApiResponse response = new ApiResponse();
        response.setDescription("""
                Automatic renewal of token information
                - Access Token If this expires, the Token information is automatically updated and passed.
                """);
        Content content = new Content();
        MediaType mediaType = new MediaType();
        Schema schema = new Schema<>();
        schema.$ref("#/components/schemas/ItemResponseTokenResponse");
        mediaType.schema(schema).example(ItemResponse.builder()
                .status("201")
                .message("토큰 정보가 자동 갱신 되었습니다.")
                .item(TokenResponse.builder()
                        .token("갱신된 Token")
                        .tokenType("Bearer")
                        .expirationSeconds(36000L).build()).build());
        content.addMediaType("application/json", mediaType);
        response.setContent(content);
        return response;
    }

    /**
     * 300 - CUD 실패 메시징 처리
     *
     * @author GEONLEE
     * @since 2024-03-22<br />
     */
    @Deprecated
    private ApiResponse logicError() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("""
                NS_ERR_02
                - request error checking from the API representative.
                
                Reason.
                - there was a problem processing the query when requesting the query.
                - if the information to be added already exists in the DB, upon further request.
                - the database does not contain information to be modified when a modification is requested.
                - when a deletion is requested, the database does not contain the information to delete.
                """);
        addContent(apiResponse, CommonErrorCode.LOGIC_ERROR);
        return apiResponse;
    }

    /**
     * response 에 content 를 추가 메서드
     *
     * @author GEONLEE
     * @since 2024-03-19<br />
     */
    @SuppressWarnings("rawtypes")
    private void addContent(ApiResponse apiResponse, ErrorCode errorCode) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        Schema schema = new Schema<>();
        schema.$ref("#/components/schemas/ErrorResponse");
        mediaType.schema(schema).example(ErrorResponse.builder()
                .status(errorCode.getResultCode())
                .message(errorCode.getResultMsg())
                .build());
        content.addMediaType("application/json", mediaType);
        apiResponse.setContent(content);
    }
}
