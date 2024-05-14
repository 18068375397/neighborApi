package kr.co.neighbor21.neighborApi.common.jpa.mapStruct;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Mapstruct 에서 사용하는 Converter
 *
 * @author GEONLEE
 * @since 2024-04-01<br />
 */
public class MapstructConverter {
    /**
     * LocalDateTime 을 받아 yyyy-MM-dd HH:mm:ss 형식의 날짜 문자열로 리턴.
     *
     * @param localDateTime 날짜.
     * @return String
     * @author LCH
     * @since 2023-06-22<br />
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) throws DateTimeParseException {
        if (localDateTime != null) {
            return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA));
        } else {
            return null;
        }
    }

    /**
     * String Y, N 을 받아 true, false 로 리턴
     *
     * @param yn Y, N String
     * @return true or false
     * @author GEONLEE
     * @since 2024-04-01<br />
     */
    public static Boolean stringToBoolean(String yn) {
        return StringUtils.equals(yn, "Y");
    }

    /**
     * true, false 를 받아 Y, N 으로 리턴
     *
     * @param is true or false
     * @return Y or N
     * @author GEONLEE
     * @since 2024-04-01<br />
     */
    public static String booleanToString(Boolean is) {
        return (is) ? "Y" : "N";
    }
}
