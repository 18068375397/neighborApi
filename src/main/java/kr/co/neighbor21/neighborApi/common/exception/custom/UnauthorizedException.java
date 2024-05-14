package kr.co.neighbor21.neighborApi.common.exception.custom;

import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;

import java.io.Serializable;

/**
 * 유지 인증 관련 처리 관련 Exception
 *
 * @author GEONLEE
 * @since 2024-03-19<br />
 * 2024-03-29 GEONLEE - errorMessage -> CommonErrorCode 로 변경<br />
 */
public class UnauthorizedException extends RuntimeException implements Serializable {

    private final CommonErrorCode commonErrorCode;
    private final Throwable cause;

    public UnauthorizedException(CommonErrorCode commonErrorCode, Throwable cause) {
        super(commonErrorCode.getResultMsg());
        this.commonErrorCode = commonErrorCode;
        this.cause = cause;
    }

    public CommonErrorCode code() {
        return this.commonErrorCode;
    }

    public Throwable cause() {
        return this.cause;
    }
}
