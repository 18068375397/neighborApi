package kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JPA Criteria 에서 사용할 조회 가능 컬럼 설정용 Annotation
 *
 * @author GEONLEE
 * @since 2023-02-14<br />
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchField {
    String[] columnName();
}
