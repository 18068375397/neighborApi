package kr.co.neighbor21.neighborApi.common.response.structure;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * API 공통 단일 객체 응답 구조체, 기존 ResponseStructure 에서 단일/복수 응답 객체로 분리
 *
 * @author GEONLEE
 * @since 2024-03-19<br />
 */
//@Builder
@Schema(description = "공통 단일 객체 응답 구조체")
public record ItemResponse<T>(
        @Schema(description = "상태 코드", example = "NS_OK")
        String status,
        @Schema(description = "응답 메시지", example = "응답 메시지")
        String message,
        @Schema(description = "단일 응답 객체 / record")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T item) {
    @Builder
    public ItemResponse(
            String status,
            String message,
            T item) {
        this.status = status;
        this.message = message;
        this.item = item;
    }
}
