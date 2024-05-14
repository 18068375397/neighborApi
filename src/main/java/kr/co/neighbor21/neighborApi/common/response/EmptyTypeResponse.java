package kr.co.neighbor21.neighborApi.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Swagger 에서 Void 사용 시 오류 표출 떄문에 빈 객체 리턴 시 사용
 *
 * @author GEON LEE
 * @since 2023-07-20<br />
 * 2024-03-06 GEONLEE - record 로 변경<br/>
 * 2024-03-06 GEONLEE - @Deprecated<br/>
 */
@Deprecated
@Schema(name = "EmptyTypeResponse - 응답 객체 타입 없을 때 사용 -> resultCode, resultMsg return.")
public record EmptyTypeResponse() {
}
