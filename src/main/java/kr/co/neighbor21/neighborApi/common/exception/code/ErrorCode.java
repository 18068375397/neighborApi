package kr.co.neighbor21.neighborApi.common.exception.code;

/**
 * Error Code Interface
 *
 * @author GEONLEE
 * @since 2022-11-09<br />
 * 2024-03-29 GEONLEE - gerResultCode String return 으로 변경<br />
 */
public interface ErrorCode {
    String getResultCode();

    String getResultMsg();
}
