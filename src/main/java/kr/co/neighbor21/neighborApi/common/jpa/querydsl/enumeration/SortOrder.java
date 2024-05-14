package kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SortDTO 의 dir 데이터 제약 처리를 위한 enum class
 *
 * @author GEONLEE
 * @since 2023-02-15<br />
 */
public enum SortOrder {
    ASC("asc"), DESC("desc");

    private static final Map<String, SortOrder> ORDER_MAP = Stream.of(values())
            .collect(Collectors.toMap(SortOrder::direction, e -> e));
    private final String direction;

    SortOrder(String direction) {
        this.direction = direction;
    }

    public static Optional<SortOrder> valueOfOrder(String direction) {
        return Optional.ofNullable(ORDER_MAP.get(direction));
    }

    public String direction() {
        return this.direction;
    }
}
