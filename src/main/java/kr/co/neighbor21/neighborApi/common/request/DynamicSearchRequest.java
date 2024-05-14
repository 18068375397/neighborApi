package kr.co.neighbor21.neighborApi.common.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;

/**
 * 공통 멀티 조건 조회용 클래스<br />
 * paging, filtering, sorting<br />
 *
 * @author GEONLEE
 * @since 2023-01-27<br />
 * 2023-02-03 GEONLEE - Generic 추가<br />
 * 2023-05-26 GEONLEE - @ToString 제거<br />
 * 2023-07-21 GEONLEE - noOffsetFilter 추가<br />
 * 2024-03-22 GEONLEE - record 로 변경<br />
 */
@Schema(description = "동적 조회 요청")
public record DynamicSearchRequest(
        @Schema(description = "가져올 개수 / default: 10", example = "10")
        Integer take,
        @Schema(description = "페이지 번호 / default: 0", example = "0")
        Integer skip,
        @Schema(description = "조회 조건 구조체")
        ArrayList<DynamicFilter> filter,
        @Schema(description = "정렬 조건 구조체")
        ArrayList<DynamicSorter> sort,
        ArrayList<DynamicFilter> noOffsetFilter) {
    public DynamicSearchRequest {
        take = (take == null) ? 1 : take;
        skip = (skip == null) ? 0 : skip;
    }
}