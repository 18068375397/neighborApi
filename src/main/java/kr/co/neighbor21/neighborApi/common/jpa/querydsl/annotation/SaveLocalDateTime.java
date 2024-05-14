package kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 추가 일시 자동 등록 ANNOTATION<br />
 * 엔티티 생성 일시에 해당 어노테이션을 등록하면 insertDynamic 에서 자동으로 현재 일시 추가<br />
 *
 * @author LCH
 * @since 2023-07-25<br />
 * 2024-03-15 GEONLEE - @Deprecated 추가, DTO record 생성자에 추가 방식으로 변경
 */
@Deprecated
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SaveLocalDateTime {

}