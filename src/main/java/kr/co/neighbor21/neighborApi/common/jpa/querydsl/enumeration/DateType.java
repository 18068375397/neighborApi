package kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration;

/**
 * DateFormat 변경 시 사용하는 ENUM CLASS.
 *
 * @author GEONLEE
 * @version 1.0.0
 * @since 2023-03-28
 * 2023-03-30 &nbsp LCH &nbsp DATE_TIME_BAR_FORMAT 추가.<br />
 * 2023-04-10 &nbsp GEONLEE &nbsp YEAR_MONTH 추가.<br />
 * 2023-05-15 &nbsp GEONLEE &nbsp DATE_FORMAT, DATE_MINUTE_FORMAT, DATE_HOUR_FORMAT, YEAR.<br />
 * 2023-06-27 &nbsp JISU &nbsp HHMI추가 <br />
 * 2023-08-16 &nbsp LCH &nbsp YYYYMMDDSIMPLE 추가. <br />
 * 2024-03-15 GEONLEE - @Deprecated 추가
 */
@Deprecated
public enum DateType {
    YYYY,
    YYYYMM,
    YYYYMMDD,
    YYYYMMDDHH,
    YYYYMMDDHHMM,
    YYYYMMDDHHMMSS,
    YYYYMMDDSIMPLE,
    HHMI
}