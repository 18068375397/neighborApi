package kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration;

import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * filterDTO 의 operator 데이터 제약 처리를 위한 enum class
 *
 * @author GEONLEE
 * @since 2023-02-15<br />
 * 2023-03-30 GEONLEE NOT_EQUAL 추가<br />
 * 2023-04-21 GEONLEE IN 추가<br />
 * 2023-04-27 LCH LESS_THAN_OR_EQUAL_TO, GREATER_THAN_OR_EQUAL_TO 추가.<br />
 */
@Getter
public enum Operator {
    EQUAL("eq"), NOT_EQUAL("neq"), LIKE("contains"), BETWEEN("between"), IN("in"), LE("lte"), NL("nl"), GE("gte"), NG("ng"), L("lt"), G("gt");

    private static final Map<String, Operator> OPERATOR_MAP = Stream.of(values())
            .collect(Collectors.toMap(Operator::type, e -> e));
    private final String type;

    Operator(String type) {
        this.type = type;
    }

    public static Optional<Operator> valueOfOperator(String operator) {
        return Optional.ofNullable(OPERATOR_MAP.get(operator));
    }

    public String type() {
        return this.type;
    }
}
