package kr.co.neighbor21.neighborApi.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 공통 멀티 조건 조회 필터 처리 클래스
 *
 * @author GEONLEE
 * @since 2023-01-27<br />
 * 2023-05-26 GEONLEE - @ToString 제거<br />
 * 2023-07-19 LCH - Setter 추가<br />
 * 2023-07-24 LYS - value 가 null 이면 toString 불가로 수정(삼항연산자 사용)<br />
 * 2023-03-22 GEONLEE - record 로 변경<br />
 */
@Schema(description = "동적 필터")
@Builder
public record DynamicFilter(
        @Schema(description = "조회 할 변수명", example = "userName")
        String field,
        @Schema(description = "조회 할 동작 [eq, contains, between, in]", example = "eq")
        String operator,
        @Schema(description = "조회 할 값", example = "이건")
        String value) {
}