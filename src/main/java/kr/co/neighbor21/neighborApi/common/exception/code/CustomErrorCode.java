package kr.co.neighbor21.neighborApi.common.exception.code;

import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * ErrorCode 구현체
 *
 * @author GEONLEE
 * @since 2023-05-03<br />
 * 2024-03-29 GEONLEE - code String 으로 변경<br />
 */
@Setter
@AllArgsConstructor
public class CustomErrorCode implements ErrorCode {

    private String code;
    private String msg;

    @Override
    public String getResultCode() {
        return this.code;
    }

    @Override
    public String getResultMsg() {
        return this.msg;
    }
}
