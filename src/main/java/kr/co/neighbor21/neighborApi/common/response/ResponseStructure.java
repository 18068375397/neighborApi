package kr.co.neighbor21.neighborApi.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * API 공통 RESPONSE STRUCTURE (BUILDER PATTERN)
 *
 * @author GEONLEE
 * @since 2022-11-08<br />
 * 2023-01-31 GEONLEE - 에러응답 처리를 위한 errors, errorSize 추가
 * 2023-02-02 GEONLEE - errors, errorSize 제거, 해당 데이터는 응답하지 않고, 로그에만 저장
 * 2023-02-24 LCH - 제네릭 타입으로 변경.
 * 2023-03-02 GEONLEE - NULL 일 때 표출 안하도록 변경 @JsonInclude(JsonInclude.Include.NON_NULL)
 * 2023-05-26 GEONLEE - @ToString 제거
 * 2024-03-04 GEONLEE - ResVO -> ResponseStructure 로 변경
 * 2024-03-19 GEONLEE - @Deprecated
 */
@Deprecated
@Schema(description = "API 공통 응답 구조체")
public class ResponseStructure<T> {
    @JsonProperty("status")
    private final int status;
    @JsonProperty("message")
    private final String message;

    @JsonProperty("size")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Long size;

    @JsonProperty("items")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<T> items;

    @JsonProperty("item")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T item;

    @Builder
    public ResponseStructure(int status, String message, Long size, List<T> items, T item) {
        this.status = status;
        this.message = message;
        this.size = size;
        this.items = items;
        this.item = item;
    }
}
