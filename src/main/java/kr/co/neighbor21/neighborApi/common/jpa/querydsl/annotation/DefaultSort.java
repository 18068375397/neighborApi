package kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation;

import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.SortOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JPA Criteria 에서 사용할 default 정렬 컬럼 설정용 Annotation
 *
 * @author GEONLEE
 * @since 2023-03-09<br />
 * 2023-03-23 GEONLEE - 복수 컬럼으로 DefaultSort 설정 가능하도록 처리 배열로 변경<br />
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultSort {
    String[] columnName();

    SortOrder[] dir();
}
