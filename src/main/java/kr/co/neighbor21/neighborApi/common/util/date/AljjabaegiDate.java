package kr.co.neighbor21.neighborApi.common.util.date;

import kr.co.neighbor21.neighborApi.common.util.CommonUtils;
import kr.co.neighbor21.neighborApi.common.util.regExp.RegExp;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 날짜 관련 CLASS. 메서드 명에 주의!!<br />
 * yyyyMMdd의 parse, format의 경우 LocalDateTime으로 할 수 없고 LocalDate로 해야함. <br/>
 * HHmmss의 parse, format의 경우 LocalDateTime으로 할 수 없고 LocalTime으로 해야함. <br/>
 * 나머지는 LocalDateTime으로 함. <br/>
 *
 * @author LCH <br />
 * @since 2023-06-13 <br />
 * 2023-06-13 &nbsp LCH &nbsp 최초 생성. <br />
 * 2023-06-22 &nbsp LCH &nbsp getLocalDateTimeRangeList 구현. <br />
 * 2023-06-22 &nbsp LCH &nbsp getDateRangeString Deprecated 처리. <br />
 * 2023-06-22 &nbsp LCH &nbsp getDateStringRangeList 구현. <br />
 * 2023-06-23 &nbsp GEONLEE &nbsp getLocalDateTimeRangeList synchronized 추가 -> searchSpec을 동시에 호출하는 경우 자원공유 문제 발생. DateTimeFormatter는 Thread safe한지 확인 필요. <br />
 * 2023-07-07 &nbsp BITNA &nbsp getBetweenDateMinutes, getDateAfterMonth 추가 -> 통계. <br />
 * 2023-07-12 &nbsp LYS &nbsp getCurrentLocalDate, dateStringToLocalDate , getLocalDateToString구현. <br />
 * 2023-07-12 &nbsp BITNA &nbsp getMonthsBetween yyyyMM 들어올 경우 처리 추가. <br />
 * 2023-08-03 &nbsp LYS &nbsp yyyy-MM-dd 형식의 문자열 현재 시각을 리턴하는 로직 추가. <br />
 * 2023-08-08 &nbsp BITNA &nbsp 사라진 함수 추가. <br />
 * 2023-08-21 &nbsp LCH &nbsp 문자열 에서 날짜로 변환 함수 통합. <br />
 * 2023-08-28 &nbsp LYS &nbsp 입력받은 날짜에서 현재 시간으로 부터의 분 차이 리턴 로직 추가. <br />
 * 2023-09-01 &nbsp JISU &nbsp NULL, 공백 체크 추가, 코드정리 <br />
 */
public class AljjabaegiDate {
    private static final String dbClassName = CommonUtils.getPropertyValue("spring.datasource.driver-class-name");

    /**
     * --- 현재시간 관련 ----------------------------------------------------------------
     */
    private static final String YYYYMMDD_FORMAT = "yyyy-MM-dd";  // LocalDate
    private static final String YYYYMMDD_SIMPLE = "yyyyMMdd";  // LocalDate
    private static final String YYYYMMDDHHMMSS_FORMAT = "yyyy-MM-dd HH:mm:ss";  // LocalDate

    /**
     * --- String -> LocalDateTime ----------------------------------------------------------------
     */
    private static final String YYYYMMDDHHMMSS_SIMPLE = "yyyyMMddHHmmss";  // LocalDate

    /**
     * @return String
     * @author LCH
     * @apiNote 현재 시각의 LocalDateTime(yyyy-MM-dd'T'HH:mm:ss)을 리턴. <br/>
     * 2023.09.01 jisu 메소드명 변경. (오버로딩이용). 주석 안맞는 부분 변경.
     * @since 2023-06-13
     */
    public static String getCurrentDateTimeString(String format) {
        LocalDateTime currentLocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format, Locale.KOREA);
        return currentLocalDateTime.format(dateTimeFormatter);
    }

    /**
     * --- String -> LocalDate ----------------------------------------------------------------
     */

    /**
     * @return String
     * @author LCH
     * @apiNote yyyy-MM-dd HH:mm:ss 형식의 문자열 현재 시각을 리턴. <br/>
     * 2023.09.01 jisu 메소드명 변경 및 코드정리
     * @since 2023-06-22
     */
    public static String getCurrentDateTimeString() {
        LocalDateTime currentLocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        return currentLocalDateTime.format(dateTimeFormatter);
    }

    /**
     * @return String
     * @author LYS
     * @apiNote yyyy-MM-dd 형식의 문자열 현재 시각을 리턴.
     * @since 2023-08-03
     */
    public static String getCurrentDateString() {
        LocalDate currentLocalDateTime = LocalDate.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA);
        return currentLocalDateTime.format(dateTimeFormatter);
    }

    /**
     * --- String -> LocalTime ----------------------------------------------------------------
     */

    /**
     * @param dateString 날짜 문자열.
     * @return LocalDateTime
     * @author LCH
     * @apiNote 지정된 형식의 날짜 문자열을 받아 LocalDateTime으로 리턴. <br />
     * 2023.09.01 jisu 코드정리. 파라메터에따라 두개로 분리(오버로딩).
     * @since 2023-06-13
     */
    public static LocalDateTime dateTimeStringToLocalDateTime(String dateString) throws DateTimeParseException, IllegalArgumentException {
        if (dateString != null && !dateString.equals("")) {
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.KOREA);
            return LocalDateTime.parse(replacedDateString, toTimeFormatter);
        } else {
            return null;
        }
    }


    /**
     * --- LocalDateTime -> String ----------------------------------------------------------------
     */

    /**
     * @param dateString 날짜 문자열.
     * @param format     dateString의 형식
     * @return LocalDateTime
     * @author LCH
     * @apiNote 지정된 형식의 날짜 문자열을 받아 LocalDateTime으로 리턴. <br />
     * @since 2023-06-13
     */
    public static LocalDateTime dateTimeStringToLocalDateTime(String dateString, String format) throws DateTimeParseException, IllegalArgumentException {
        if (dateString != null && !dateString.equals("")) {
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toTimeFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            return LocalDateTime.parse(replacedDateString, toTimeFormatter);
        } else {
            return null;
        }

    }

    /**
     * @param dateString 날짜 문자열.
     * @return LocalDate
     * @author LYS
     * @apiNote yyyyMMdd 형식의 날짜 문자열을 받아 LocalDate로 리턴.
     * @since 2023-07-12
     */
    public static LocalDate dateStringToLocalDate(String dateString) throws DateTimeParseException, IllegalArgumentException {
        if (dateString != null && !dateString.equals("")) {
            String replacedDateString = getOnlyNumbers(dateString);
            return LocalDate.parse(replacedDateString, DateTimeFormatter.ofPattern("yyyyMMdd", Locale.KOREA));
        } else {
            return null;
        }
    }

    /**
     * --- LocalDate -> String ----------------------------------------------------------------
     */

    /**
     * @param dateString 날짜 문자열.
     * @param format     dateString의 형식.
     * @return LocalDate
     * @author LYS
     * @apiNote 지정된 형식의 날짜 문자열을 받아 LocalDate로 리턴.
     * @since 2023-07-12
     */
    public static LocalDate dateStringToLocalDate(String dateString, String format) throws DateTimeParseException, IllegalArgumentException {
        if (dateString != null && !dateString.equals("")) {
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toDateFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            return LocalDate.parse(replacedDateString, toDateFormatter);
        } else {
            return null;
        }
    }

    /**
     * @param dateString 날짜 문자열.
     * @param format     dateString의 형식.
     * @return LocalDate
     * @author jisu
     * @apiNote 지정된 형식의 날짜 문자열을 받아 LocalTime로 리턴.
     * @since 2023-08-01
     */
    public static LocalTime dateStringToLocalTime(String dateString, String format) throws DateTimeParseException, IllegalArgumentException {
        if (dateString != null && !dateString.equals("")) {
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toDateFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            return LocalTime.parse(replacedDateString, toDateFormatter);
        } else {
            return null;
        }
    }

    /**
     * --- LocalTime -> String ----------------------------------------------------------------
     */

    /**
     * @param localDateTime 날짜.
     * @return String
     * @author LCH
     * @apiNote LocalDateTime을 받아 yyyy-MM-dd HH:mm:ss 형식의 날짜 문자열로 리턴.
     * @since 2023-06-22
     */
    public static String getLocalDateTimeToString(LocalDateTime localDateTime) throws DateTimeParseException {
        if (localDateTime != null) {
            return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA));
        } else {
            return null;
        }
    }

    /**
     * --- String -> String ----------------------------------------------------------------
     */

    /**
     * @param localDateTime 날짜.
     * @param format        받고싶은 날짜 문자열의 형식.
     * @return String
     * @author LCH
     * @apiNote LocalDateTime을 받아 지정된 형식의 날짜 문자열로 리턴.
     * @since 2023-06-13
     */
    public static String getLocalDateTimeToString(LocalDateTime localDateTime, String format) throws DateTimeParseException {
        if (localDateTime != null) {
            return DateTimeFormatter.ofPattern(format, Locale.KOREA).format(localDateTime);
        } else {
            return null;
        }
    }

    /**
     * @param localDate 날짜.
     * @return String
     * @author LYS
     * @apiNote LocalDate을 받아 yyyy-MM-dd 형식의 날짜 문자열로 리턴.
     * @since 2023-07-12
     */
    public static String getLocalDateToString(LocalDate localDate) throws DateTimeParseException {
        if (localDate != null) {
            return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA));
        } else {
            return null;
        }
    }

    /**
     * @param localDate 날짜.
     * @param format    받고싶은 날짜 문자열의 형식.
     * @return String
     * @author LYS
     * @apiNote LocalDate을 받아 지정된 형식의 날짜 문자열로 리턴.
     * @since 2023-07-12
     */
    public static String getLocalDateToString(LocalDate localDate, String format) throws DateTimeParseException {
        if (localDate != null) {
            return DateTimeFormatter.ofPattern(format, Locale.KOREA).format(localDate);
        } else {
            return null;
        }
    }

    /**
     * @param localTime 날짜.
     * @param format    받고싶은 날짜 문자열의 형식.
     * @return String
     * @author jisu
     * @apiNote LocalTime을 받아 지정된 형식의 날짜 문자열로 리턴.
     * @since 2023-09-01
     */
    public static String getLocalTimeToString(LocalTime localTime, String format) throws DateTimeParseException {
        if (localTime != null) {
            return DateTimeFormatter.ofPattern(format, Locale.KOREA).format(localTime);
        } else {
            return null;
        }
    }

    /**
     * @param dateString 날짜 문자열.
     * @return String
     * @author LCH
     * 포맷되지 않은 날짜 문자열을 받아 지정된 형식의 날짜 문자열로 리턴. LocalDateTime사용
     * yyyyMMdd HHmmss인경우
     * @since 2023-06-22
     */
    public static String getFormattedDateTimeString(String dateString) throws DateTimeParseException {
        if (dateString != null && !dateString.equals("")) {
            String format = "yyyy-MM-dd HH:mm:ss";
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toDateFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            DateTimeFormatter toStringFormatter = DateTimeFormatter.ofPattern(format, Locale.KOREA);
            LocalDateTime parse = LocalDateTime.parse(replacedDateString, toDateFormatter);
            return toStringFormatter.format(parse);
        } else {
            return null;
        }
    }

    /**
     * @param dateString 날짜 문자열.
     * @param format     넘긴 문자열의 형식.
     * @return String
     * @author LCH
     * 포맷되지 않은 날짜 문자열을 받아 지정된 형식의 날짜 문자열로 리턴. LocalDateTime사용
     * yyyyMMdd HHmmss인경우
     * @since 2023-06-22
     */
    public static String getFormattedDateTimeString(String dateString, String format) throws DateTimeParseException {
        if (dateString != null && !dateString.equals("")) {
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toDateFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            DateTimeFormatter toStringFormatter = DateTimeFormatter.ofPattern(format, Locale.KOREA);
            LocalDateTime parse = LocalDateTime.parse(replacedDateString, toDateFormatter);
            return toStringFormatter.format(parse);
        } else {
            return null;
        }
    }

    /**
     * --- 그외 ----------------------------------------------------------------
     */

    /**
     * @param dateString 날짜 문자열.
     * @return String
     * @author LCH
     * 포맷되지 않은 날짜 문자열을 받아 yyyy-MM-dd 날짜 문자열로 리턴. LocalDate사용
     * yyyyMMdd등 날짜까지만 쓰는경우
     * @since 2023-06-22
     */
    public static String getFormattedDateString(String dateString) throws DateTimeParseException {
        if (dateString != null && !dateString.equals("")) {
            String format = "yyyy-MM-dd";
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toDateFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            DateTimeFormatter toStringFormatter = DateTimeFormatter.ofPattern(format, Locale.KOREA);
            LocalDate parse = LocalDate.parse(replacedDateString, toDateFormatter);
            return toStringFormatter.format(parse);
        } else {
            return null;
        }
    }

    /**
     * @param dateString 날짜 문자열.
     * @param format     넘긴 문자열의 형식.
     * @return String
     * @author LCH
     * 포맷되지 않은 날짜 문자열을 받아 지정된 형식의 날짜 문자열로 리턴. LocalDate사용
     * yyyyMMdd등 날짜까지만 쓰는경우
     * @since 2023-06-22
     */
    public static String getFormattedDateString(String dateString, String format) throws DateTimeParseException {
        if (dateString != null && !dateString.equals("")) {
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toDateFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            DateTimeFormatter toStringFormatter = DateTimeFormatter.ofPattern(format, Locale.KOREA);
            LocalDate parse = LocalDate.parse(replacedDateString, toDateFormatter);
            return toStringFormatter.format(parse);
        } else {
            return null;
        }
    }

    /**
     * @param dateString 날짜 문자열.
     * @return String
     * @author jisu
     * 포맷되지 않은 날짜 문자열을 받아 HH:mm:ss의 날짜 문자열로 리턴. LocalTime사용
     * HHmmss등 시간만 쓰는경우
     * @since 2023-08-31
     */
    public static String getFormattedTimeString(String dateString) throws DateTimeParseException {
        if (dateString != null && !dateString.equals("")) {
            String format = "HH:mm:ss";
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toDateFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            DateTimeFormatter toStringFormatter = DateTimeFormatter.ofPattern(format, Locale.KOREA);
            LocalTime parse = LocalTime.parse(replacedDateString, toDateFormatter);
            return toStringFormatter.format(parse);
        } else {
            return null;
        }
    }

    /**
     * @param dateString 날짜 문자열.
     * @param format     넘긴 문자열의 형식.
     * @return String
     * @author jisu
     * 포맷되지 않은 날짜 문자열을 받아 지정된 형식의 날짜 문자열로 리턴. LocalTime사용
     * HHmmss등 시간만 쓰는경우
     * @since 2023-08-31
     */
    public static String getFormattedTimeString(String dateString, String format) throws DateTimeParseException {
        if (dateString != null && !dateString.equals("")) {
            String replacedDateString = getOnlyNumbers(dateString);
            DateTimeFormatter toDateFormatter = DateTimeFormatter.ofPattern(getOnlyAlphabets(format), Locale.KOREA);
            DateTimeFormatter toStringFormatter = DateTimeFormatter.ofPattern(format, Locale.KOREA);
            LocalTime parse = LocalTime.parse(replacedDateString, toDateFormatter);
            return toStringFormatter.format(parse);
        } else {
            return null;
        }
    }

    /**
     * 시작 날짜와 끝 날짜 사이에 모든 날을 리턴.
     *
     * @param startDate 시작 날짜
     * @param endDate   끝 날짜
     * @return date List
     * @author LCH
     * @since 2023-05-22
     */
    public static List<String> getDatesBetween(String startDate, String endDate) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate currentDate = LocalDate.parse(startDate, formatter);
        LocalDate finalDate = LocalDate.parse(endDate, formatter);


        while (!currentDate.isAfter(finalDate)) {
            dates.add(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

    /**
     * 시작 날짜와 끝 날짜 사이에 모든 월을 리턴.
     *
     * @param startMonth 시작 날짜
     * @param endMonth   끝 날짜
     * @return date List
     * @author LCH
     * @since 2023-05-22
     */
    public static List<String> getMonthsBetween(String startMonth, String endMonth) {
        List<String> months = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

        if (startMonth.length() > 6) {
            formatter = DateTimeFormatter.ofPattern("yyyyMM");
        }
        YearMonth currentMonth = YearMonth.parse(startMonth, formatter);
        YearMonth finalMonth = YearMonth.parse(endMonth, formatter);

        while (!currentMonth.isAfter(finalMonth)) {
            months.add(currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            currentMonth = currentMonth.plusMonths(1);
        }

        return months;
    }

    /**
     * 시작 날짜와 끝 날짜 사이에 모든 분기를 리턴.
     *
     * @param startDate 시작 날짜
     * @param endDate   끝 날짜
     * @return date List
     * @author LCH
     * @since 2023-05-22
     */
    public static List<String> getQuartersBetween(String startDate, String endDate) {
        List<String> quarters = new ArrayList<>();
        List<String> months = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        YearMonth currentMonth = YearMonth.parse(startDate, formatter);
        YearMonth finalMonth = YearMonth.parse(endDate, formatter);

        while (!currentMonth.isAfter(finalMonth)) {
            months.add(currentMonth.format(DateTimeFormatter.ofPattern("yyyy-q")));
            currentMonth = currentMonth.plusMonths(1);
        }

        quarters = months.stream().distinct().toList();

        return quarters;
    }

    /**
     * 시작 날짜와 끝 날짜 사이에 모든 연도를 리턴.
     *
     * @param start 시작 날짜
     * @param end   끝 날짜
     * @return date List
     * @author LCH
     * @since 2023-05-22
     */
    public static List<String> getYearsBetween(String start, String end) {
        List<String> years = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        Year currentYear = Year.parse(start, formatter);
        Year finalYear = Year.parse(end, formatter);

        while (!currentYear.isAfter(finalYear)) {
            years.add(currentYear.format(DateTimeFormatter.ofPattern("yyyy")));
            currentYear = currentYear.plusYears(1);
        }

        return years;
    }

    /* ---------------------------------------------------------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------------------------------------------------- */

    /**
     * 입력받은 날짜에서 (+한 달-1일) 뒤 날짜를 리턴
     *
     * @return date string
     * @author BITNA
     * @since 2023-04-24
     */
    public static LocalDateTime getDateAfterMonth(LocalDateTime date) {
        date = date.plusMonths(1);
        date = date.plusDays(-1);

        return date;
    }

    /**
     * 입력받은 날짜에서 (+한 달-1일) 뒤 날짜를 리턴
     *
     * @return date string
     * @author BITNA
     * @since 2023-04-24
     */
    public static long getBetweenDateMinutes(LocalDateTime startDt, LocalDateTime endDt) {
        Duration duration = Duration.between(startDt, endDt);

        return duration.getSeconds() / 60;
    }

    /**
     * 입력받은 날짜에서 (+1 년 -1일) 뒤 날짜를 리턴
     *
     * @param date
     * @return date string
     * @author BITNA
     * @since 2023-07-18
     */
    public static LocalDateTime getDateAfterYear(LocalDateTime date) {
        date = date.plusYears(1);
        date = date.plusDays(-1);

        return date;
    }

    /**
     * 날짜가 몇 분기에 속해있는지 리턴.
     *
     * @param date 끝나는 날짜.
     * @return date List
     * @author LCH
     * @since 2023-04-27
     */
    public static String getQuarterOfYear(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-q");

        return formatter.format(date);
    }

    /**
     * @return LocalDate
     * @author LCH
     * @apiNote 현재 시각의 LocalDate 리턴.
     * @since 2023-08-16
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now(ZoneId.of("Asia/Seoul"));
    }

    /**
     * @return String
     * @author LCH
     * @apiNote 현재 시각의 yyyy-MM-dd 문자열 리턴.
     * @since 2023-08-16
     */
    public static String getCurrentLocalDateFormatString() {
        return LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern(YYYYMMDD_FORMAT, Locale.KOREA));
    }

    /**
     * @return String
     * @author LCH
     * @apiNote 현재 시각의 yyyyMMdd 문자열 리턴.
     * @since 2023-08-16
     */
    public static String getCurrentLocalDateSimpleString() {
        return LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern(YYYYMMDD_SIMPLE, Locale.KOREA));
    }

    //    /**
    //     * @param string LocalDate로 변환하고자 하는 포맷 문자열
    //     * @return LocalDate
    //     * @since 2023-08-16
    //     * @author LCH
    //     * @apiNote 주어진 포맷 문자열을 LocalDate로 리턴.
    //     */
    //    public static LocalDate formatStringToLocalDate(String string) {
    //        return LocalDate.parse(string, DateTimeFormatter.ofPattern(YYYYMMDD_FORMAT, Locale.KOREA));
    //    }

    /**
     * @param string LocalDate로 변환하고자 하는 심플 문자열
     * @return LocalDate
     * @author LCH
     * @apiNote 주어진 심플 문자열을 LocalDate로 리턴.
     * @since 2023-08-16
     */
    public static LocalDate stringToLocalDate(String string) {
        return LocalDate.parse(getOnlyNumbers(string), DateTimeFormatter.ofPattern(YYYYMMDD_SIMPLE, Locale.KOREA));
    }

    /**
     * @param localDate 문자열로 변환하고자 하는 LocalDate
     * @return String
     * @author LCH
     * @apiNote 주어진 시각의 yyyy-MM-dd 문자열 리턴.
     * @since 2023-08-16
     */
    public static String localDateToFormatString(LocalDate localDate) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(YYYYMMDD_FORMAT, Locale.KOREA)) : null;
    }

    /**
     * @param localDate 문자열로 변환하고자 하는 LocalDate
     * @return String
     * @author LCH
     * @apiNote 주어진 시각의 yyyyMMdd 문자열 리턴.
     * @since 2023-08-16
     */
    public static String localDateToSimpleString(LocalDate localDate) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern(YYYYMMDD_SIMPLE, Locale.KOREA)) : null;
    }

    /**
     * @param range 심플 문자열 날짜 두개를 콤마로 연결해 둔 문자열
     * @return List
     * @author LCH
     * @apiNote 주어진 범위의 문자열을 LocalDate 리스트로 만들어 리턴.
     * @since 2023-08-16
     */
    public static List<LocalDate> getLocalDateRange(String range) {
        String range1 = range.split(",")[0];
        String range2 = range.split(",")[1];

        return List.of(stringToLocalDate(range1), stringToLocalDate(range2));
    }

    /**
     * @param range 심플 문자열 날짜 두개를 콤마로 연결해 둔 문자열
     * @return List
     * @author LCH
     * @apiNote 주어진 범위의 문자열을 LocalDate 문자열 리스트로 만들어 리턴.
     * @since 2023-08-16
     */
    public static List<String> getLocalDateStringRange(String range) {
        String range1 = range.split(",")[0];
        String range2 = range.split(",")[1];

        return List.of(range1, range2);
    }

    /* ---------------------------------------------------------------------------------------------------------------------------------------------------- */

    /**
     * @return LocalDateTime
     * @author LCH
     * @apiNote 현재 시각의 LocalDateTime 리턴.
     * @since 2023-08-16
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

//    /**
//     * @return LocalDateTime
//     * @since 2023-08-16
//     * @author jisu
//     * @apiNote 현재 시각의 LocalDate 리턴.
//     */
//    public static LocalDate getCurrentLocalDate() {
//        return LocalDate.now(ZoneId.of("Asia/Seoul"));
//    }

    /**
     * @return String
     * @author LCH
     * @apiNote 현재 시각의 yyyy-MM-dd HH:mm:ss 문자열 리턴.
     * @since 2023-08-16
     */
    public static String getCurrentLocalDateTimeFormatString() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS_FORMAT, Locale.KOREA));
    }

    /**
     * @return String
     * @author LCH
     * @apiNote 현재 시각의 yyyyMMddHHmmss 문자열 리턴.
     * @since 2023-08-16
     */
    public static String getCurrentLocalDateTimeSimpleString() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS_SIMPLE, Locale.KOREA));
    }

    //    /**
    //     * @param string LocalDateTime으로 변환하고자 하는 포맷 문자열
    //     * @return LocalDateTime
    //     * @since 2023-08-16
    //     * @author LCH
    //     * @apiNote 주어진 포맷 문자열을 LocalDateTime으로 리턴.
    //     */
    //    public static LocalDateTime formatStringToLocalDateTime(String string) {
    //        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS_FORMAT, Locale.KOREA));
    //    }

    /**
     * @param string LocalDateTime으로 변환하고자 하는 심플 문자열
     * @return LocalDate
     * @author LCH
     * @apiNote 주어진 심플 문자열을 LocalDateTime으로 리턴.
     * @since 2023-08-16
     */
    public static LocalDateTime stringToLocalDateTime(String string) {
        return LocalDateTime.parse(getOnlyNumbers(string), DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS_SIMPLE, Locale.KOREA));
    }

    /**
     * @param localDateTime 문자열로 변환하고자 하는 LocalDateTime
     * @return String
     * @author LCH
     * @apiNote 주어진 시각의 yyyy-MM-dd HH:mm:ss 문자열 리턴.
     * @since 2023-08-16
     */
    public static String localDateTimeToFormatString(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS_FORMAT, Locale.KOREA)) : null;
    }

    /**
     * @param localDateTime 문자열로 변환하고자 하는 LocalDateTime
     * @return String
     * @author LCH
     * @apiNote 주어진 시각의 yyyyMMddHHmmss 문자열 리턴.
     * @since 2023-08-16
     */
    public static String localDateTimeToSimpleString(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS_SIMPLE, Locale.KOREA)) : null;
    }

    /**
     * @param range 심플 문자열 날짜 두개를 콤마로 연결해 둔 문자열
     * @return List
     * @author LCH
     * @apiNote 주어진 범위의 문자열을 LocalDateTime 리스트로 만들어 리턴.
     * @since 2023-08-16
     */
    public static List<LocalDateTime> getLocalDateTimeRange(String range) throws IndexOutOfBoundsException {
        String range1 = range.split(",")[0];
        String range2 = range.split(",")[1];

        return List.of(stringToLocalDateTime(range1), stringToLocalDateTime(range2));
    }

    /**
     * @param range 심플 문자열 날짜 두개를 콤마로 연결해 둔 문자열
     * @return List
     * @author LCH
     * @apiNote 주어진 범위의 문자열을 LocalDateTime 문자열 리스트로 만들어 리턴.
     * @since 2023-08-16
     */
    public static List<String> getLocalDateTimeStringRange(String range) {
        String range1 = range.split(",")[0];
        String range2 = range.split(",")[1];

        return List.of(range1, range2);
    }

    /**
     * @param string 심플 문자열
     * @return String
     * @author LCH
     * @apiNote 심플 문자열을 포맷 문자열로 만들어 리턴.
     * @since 2023-08-16
     */
    public static String simpleStringToFormatString(String string) {
        return string.length() == 8
                ? string.substring(0, 4) + "-" + string.substring(4, 6) + "-" + string.substring(6, 8)
                : string.length() == 14
                ? string.substring(0, 4) + "-" + string.substring(4, 6) + "-" + string.substring(6, 8) + " " +
                string.substring(8, 10) + ":" + string.substring(10, 12) + ":" + string.substring(12, 14)
                : null;
    }

    /**
     * @param string 심플 문자열
     * @return String
     * @author LCH
     * @apiNote 포맷 문자열을 심플 문자열로 만들어 리턴.
     * @since 2023-08-16
     */
    public static String formatStringToSimpleString(String string) {
        return string.length() == 10
                ? string.replace("-", "")
                : string.length() == 19
                ? string.replace("-", "").replace(":", "").replace(" ", "")
                : null;
    }

    /**
     * 입력받은 날짜에서 현재 시간으로 부터의 분 차이 리턴
     *
     * @return long
     * @author LYS
     * @since 2023-08-28
     */
    public static long getBetweenDateMinutesFromNow(LocalDateTime date) {
        Duration duration = Duration.between(date, getCurrentLocalDateTime());
        long minutesDifference = duration.toMinutes();

        return minutesDifference;
    }

    /**
     * @param dateRangeString 날짜 2개를 콤마로 이어둔 문자열.
     * @return List<LocalDateTime>
     * @author LCH
     * 날짜 문자열을 받아 LocalDateTime 리스트로 리턴.
     * @since 2023-06-22
     */
    public static synchronized List<LocalDateTime> getLocalDateTimeRangeList(String dateRangeString) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        String startDateString = getOnlyNumbers(dateRangeString.split(",")[0]);
        String endDateString = getOnlyNumbers(dateRangeString.split(",")[1]);

        if (startDateString.length() != endDateString.length()) {  // 시작 날짜와, 종료 날짜의 길이(형식)이 서로 다른 경우.
            throw new IllegalArgumentException();
        }
        if (startDateString.length() != 4 && startDateString.length() != 6 && startDateString.length() != 8 && startDateString.length() != 10 && startDateString.length() != 12 && startDateString.length() != 14) {  // 년(4), 월(6), 일(8), 시(10), 분(12), 초(14).
            throw new IllegalArgumentException();
        }

        StringBuilder start = new StringBuilder();
        StringBuilder end = new StringBuilder();
        switch (startDateString.length()) {
            case 4: {
                return List.of(dateTimeStringToLocalDateTime(start.append(startDateString).append("0101000000").toString()),
                        dateTimeStringToLocalDateTime(end.append(endDateString).append("1231").append("235959").toString()));
            }

            case 6: {
                String lengthOfMonth = String.valueOf(YearMonth.of(
                                Integer.parseInt(endDateString.substring(0, 4)),
                                Integer.parseInt(endDateString.substring(4)))
                        .lengthOfMonth());
                if (lengthOfMonth.length() < 2) {
                    lengthOfMonth = "0" + lengthOfMonth;
                }

                return List.of(dateTimeStringToLocalDateTime(start.append(startDateString).append("01000000").toString()),
                        dateTimeStringToLocalDateTime(end.append(endDateString).append(lengthOfMonth).append("235959").toString()));
            }

            case 8: {
                return List.of(dateTimeStringToLocalDateTime(start.append(startDateString).append("000000").toString()),
                        dateTimeStringToLocalDateTime(end.append(endDateString).append("235959").toString()));
            }

            case 10: {
                return List.of(dateTimeStringToLocalDateTime(start.append(startDateString).append("0000").toString()),
                        dateTimeStringToLocalDateTime(end.append(endDateString).append("5959").toString()));
            }

            case 12: {
                return List.of(dateTimeStringToLocalDateTime(start.append(startDateString).append("00").toString()),
                        dateTimeStringToLocalDateTime(end.append(endDateString).append("59").toString()));
            }

            case 14: {
                return List.of(dateTimeStringToLocalDateTime(start.append(startDateString).toString()),
                        dateTimeStringToLocalDateTime(end.append(endDateString).toString()));
            }


            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * --- range ----------------------------------------------------------------
     */

    /**
     * @param dateRangeString 날짜 2개를 콤마로 이어둔 문자열.
     * @return List<String>
     * @author LCH
     * 날짜 문자열을 받아 yyyyMMddHHmmss 형식의 문자열 리스트로 리턴.
     * @apiNote 2023-08-08 GEON LEE db 타입에 따라 return 값 다르게 처리, postgresql 에서는 날자 길이 값보다 길게 보내면 조회 안됨.
     * @since 2023-06-22
     */
    public static List<String> getDateStringRangeList(String dateRangeString) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        String startDateString = getOnlyNumbers(dateRangeString.split(",")[0]);
        String endDateString = getOnlyNumbers(dateRangeString.split(",")[1]);

        if (startDateString.length() != endDateString.length()) {  // 시작 날짜와, 종료 날짜의 길이(형식)이 서로 다른 경우.
            throw new IllegalArgumentException();
        }
        if (startDateString.length() != 4 && startDateString.length() != 6 && startDateString.length() != 8 && startDateString.length() != 10 && startDateString.length() != 12 && startDateString.length() != 14) {  // 년(4), 월(6), 일(8), 시(10), 분(12), 초(14).
            throw new IllegalArgumentException();
        }

        StringBuilder start = new StringBuilder();
        StringBuilder end = new StringBuilder();
        switch (startDateString.length()) {
            case 4: {
                List<String> rangeList;
                if ("org.postgresql.Driver".equals(dbClassName)) {
                    rangeList = List.of(start.append(startDateString).toString(),
                            end.append(endDateString).toString());
                } else {
                    rangeList = List.of(start.append(startDateString).append("0101000000").toString(),
                            end.append(endDateString).append("1231235959").toString());
                }
                return rangeList;
            }

            case 6: {
                List<String> rangeList;
                String lengthOfMonth = String.valueOf(YearMonth.of(
                                Integer.parseInt(endDateString.substring(0, 4)),
                                Integer.parseInt(endDateString.substring(4)))
                        .lengthOfMonth());
                if (lengthOfMonth.length() < 2) {
                    lengthOfMonth = "0" + lengthOfMonth;
                }
                if ("org.postgresql.Driver".equals(dbClassName)) {
                    rangeList = List.of(start.append(startDateString).toString(),
                            end.append(endDateString).append(lengthOfMonth).toString());
                } else {
                    rangeList = List.of(start.append(startDateString).append("01000000").toString(),
                            end.append(endDateString).append(lengthOfMonth).append("235959").toString());
                }
                return rangeList;
            }

            case 8: {
                List<String> rangeList;
                if ("org.postgresql.Driver".equals(dbClassName)) {
                    rangeList = List.of(start.append(startDateString).toString(),
                            end.append(endDateString).toString());
                } else {
                    rangeList = List.of(start.append(startDateString).append("000000").toString(),
                            end.append(endDateString).append("235959").toString());
                }
                return rangeList;
            }

            case 10: {
                List<String> rangeList;
                if ("org.postgresql.Driver".equals(dbClassName)) {
                    rangeList = List.of(start.append(startDateString).toString(),
                            end.append(endDateString).toString());
                } else {
                    rangeList = List.of(start.append(startDateString).append("0000").toString(),
                            end.append(endDateString).append("5959").toString());
                }
                return rangeList;
            }

            case 12: {
                List<String> rangeList;
                if ("org.postgresql.Driver".equals(dbClassName)) {
                    rangeList = List.of(start.append(startDateString).toString(),
                            end.append(endDateString).toString());
                } else {
                    rangeList = List.of(start.append(startDateString).append("00").toString(),
                            end.append(endDateString).append("59").toString());
                }
                return rangeList;
            }

            case 14: {
                return List.of(start.append(startDateString).toString(),
                        end.append(endDateString).toString());
            }

            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * DEPRECATED
     */

    /**
     * @param localDateTime 기준 날짜.
     * @param days          더할 일 수.
     * @return LocalDateTime
     * @author LCH
     * @Description 기준 날짜에 days만큼 더한 LocalDateTime 리턴.
     * @since 2023-06-13
     */
    public static LocalDateTime getPlusDaysLocalDateTime(LocalDateTime localDateTime, Integer days) {
        return localDateTime.plusDays(days);
    }

    /**
     * @param localDateTime 기준 날짜.
     * @param days          뺄 일 수.
     * @return LocalDateTime
     * @author LCH
     * @Description 기준 날짜에 days만큼 뺀 LocalDateTime 리턴.
     * @since 2023-06-13
     */
    public static LocalDateTime getMinusDaysLocalDateTime(LocalDateTime localDateTime, Integer days) {
        return localDateTime.minusDays(days);
    }

    /**
     * @param localDateTime 기준 날짜.
     * @param months        더할 월 수.
     * @return LocalDateTime
     * @author LCH
     * @Description 기준 날짜에 months만큼 더한 LocalDateTime 리턴.
     * @since 2023-06-13
     */
    public static LocalDateTime getPlusMonthsLocalDateTime(LocalDateTime localDateTime, Integer months) {
        return localDateTime.plusMonths(months);
    }

    /**
     * @param localDateTime 기준 날짜.
     * @param months        뺄 월 수.
     * @return LocalDateTime
     * @author LCH
     * @Description 기준 날짜에 months만큼 뺀 LocalDateTime 리턴.
     * @since 2023-06-13
     */
    public static LocalDateTime getMinusMonthsLocalDateTime(LocalDateTime localDateTime, Integer months) {
        return localDateTime.minusMonths(months);
    }

    /**
     * @param localDateTime 기준 날짜.
     * @param years         더할 년 수.
     * @return LocalDateTime
     * @author LCH
     * @Description 기준 날짜에 years만큼 더한 LocalDateTime 리턴.
     * @since 2023-06-13
     */
    public static LocalDateTime getPlusYearsLocalDateTime(LocalDateTime localDateTime, Integer years) {
        return localDateTime.plusYears(years);
    }

    /**
     * @param localDateTime 기준 날짜.
     * @param years         뺄 년 수.
     * @return LocalDateTime
     * @author LCH
     * @Description 기준 날짜에 years만큼 뺀 LocalDateTime 리턴.
     * @since 2023-06-13
     */
    public static LocalDateTime getMinusYearsLocalDateTime(LocalDateTime localDateTime, Integer years) {
        return localDateTime.minusYears(years);
    }

    public static List<LocalDateTime> getComputeRangeList(String dateString) throws IllegalArgumentException {
        if (dateString.contains(",")) {
            String dateString1 = dateString.split(",")[0];
            String dateString2 = dateString.split(",")[1];

            if (dateString1.length() == 8) {
                return List.of(dateTimeStringToLocalDateTime(dateString1 + "000000"), dateTimeStringToLocalDateTime(dateString2 + "000000").plusDays(1));
            } else if (dateString1.length() == 10) {
                return List.of(dateTimeStringToLocalDateTime(dateString1 + "0000"), dateTimeStringToLocalDateTime(dateString2 + "0000").plusHours(1));
            } else if (dateString1.length() == 12) {
                return List.of(dateTimeStringToLocalDateTime(dateString1 + "00"), dateTimeStringToLocalDateTime(dateString2 + "00").plusMinutes(1));
            } else {
                throw new IllegalArgumentException("Date String length must be 8 or 10 or 12.");
            }
        } else {
            LocalDateTime localDateTime;
            if (dateString.length() == 8) {
                localDateTime = dateTimeStringToLocalDateTime(dateString + "000000");
                return List.of(localDateTime, localDateTime.plusDays(1));
            } else if (dateString.length() == 10) {
                localDateTime = dateTimeStringToLocalDateTime(dateString + "0000");
                return List.of(localDateTime, localDateTime.plusHours(1));
            } else if (dateString.length() == 12) {
                localDateTime = dateTimeStringToLocalDateTime(dateString + "00");
                return List.of(localDateTime, localDateTime.plusMinutes(1));
            } else {
                throw new IllegalArgumentException("Date String length must be 8 or 10 or 12.");
            }
        }
    }

    /**
     * --- 기능함수 ---------------------------------------------------------------------------------------------------------------
     */

    /**
     * @param str 문자열.
     * @return String
     * @author LCH
     * 숫자만 뽑아서 return
     * @since 2023-06-13
     */
    public static String getOnlyNumbers(String str) {
        return str.replaceAll(RegExp.ONLY_NUMBER, "");
    }

    /**
     * @param str 문자열.
     * @return String
     * @author jisu
     * 영어만 뽑아서 return
     * @since 2023-09-01
     */
    public static String getOnlyAlphabets(String str) {
        return str.replaceAll(RegExp.ONLY_ENG, "");
    }
}