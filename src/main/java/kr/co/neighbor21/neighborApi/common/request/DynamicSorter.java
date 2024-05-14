package kr.co.neighbor21.neighborApi.common.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공통 멀티 조건 조회 정렬 처리 클래스
 *
 * @author GEONLEE
 * @since 2023-01-27<br />
 * 2023-05-26 GEONLEE - @ToString 제거<br />
 * 2024-03-22 GEONLEE - record 로 변경<br />
 */
@Schema(description = "동적 정렬")
public record DynamicSorter(
        @Schema(description = "정렬할 변수명", example = "modifyDate")
        String field,
        @Schema(description = "정렬할 방향", example = "desc")
        String dir) {
}
