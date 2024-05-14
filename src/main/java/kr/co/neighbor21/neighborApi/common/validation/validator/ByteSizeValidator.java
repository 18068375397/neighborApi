package kr.co.neighbor21.neighborApi.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.neighbor21.neighborApi.common.validation.annotation.ByteSize;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;

/**
 * 바이트 크기 검증 VALIDATOR
 *
 * @author LCH <br />
 * @since 2023-05-31 <br />
 * 2023-05-31 &nbsp LCH &nbsp 최초 생성. <br />
 */

public class ByteSizeValidator implements ConstraintValidator<ByteSize, String> {
    private int min;
    private int max;

    @Override
    public void initialize(ByteSize annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    @Override
    @SneakyThrows
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        int byteSize = value.getBytes(StandardCharsets.UTF_8).length;

        return byteSize >= min && byteSize <= max;
    }
}