package kr.co.neighbor21.neighborApi.common.exception.custom;

import kr.co.neighbor21.neighborApi.common.exception.code.ErrorCode;

import java.io.Serial;

/**
 * RuntimeException 처리용 Exception<br />
 * Internal Server Exception 전달 방지용
 * 모든 서비스 로직에서 해당 Exception 을 throw
 *
 * @author GEONLEE
 * @since 2022-11-09<br />
 * 2023-05-03 GEONLEE - final 제거<br />
 * 2024-03-29 GEONLEE - 로그에 Exception message 표출되도록 수정<br />
 */
public class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public final ErrorCode errorCode;


    public ServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getResultMsg(), cause);
        this.errorCode = errorCode;
    }

}
