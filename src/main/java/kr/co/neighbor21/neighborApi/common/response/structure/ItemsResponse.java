package kr.co.neighbor21.neighborApi.common.response.structure;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * API 공통 복수 객체 응답 구조체, 기존 ResponseStructure 에서 단일/복수 응답 객체로 분리
 *
 * @author GEONLEE
 * @since 2024-03-19<br />
 */
@Builder
@Schema(description = "공통 복수 객체 응답 구조체")
public record ItemsResponse<T>(
        @Schema(description = "상태 코드", example = "NS_OK")
        String status,
        @Schema(description = "응답 메시지", example = "응답 메시지")
        String message,
        @Schema(description = "응답 데이터 사이즈", example = "1")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long size,
        @Schema(description = "복수 응답 객체 / LIST")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<T> items) {
}
