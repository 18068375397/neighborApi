package kr.co.neighbor21.neighborApi.common.util;

import jakarta.servlet.ServletResponse;
import kr.co.neighbor21.neighborApi.common.contextHolder.ApplicationContextHolder;
import kr.co.neighbor21.neighborApi.common.response.structure.ErrorResponse;
import kr.co.neighbor21.neighborApi.common.variable.CommonVariables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 공통 static method class
 * 공통으로 사용할 method 만 설정, variable 은 CommonVariable 로 이동 (정리 필요)
 *
 * @author GEONLEE
 * @since 2022-11-07<br />
 */
public class CommonUtils {
    protected static Logger logger = LogManager.getLogger(CommonUtils.class);

    /**
     * Property 값에 접근하는 @Value 는 spring bean 에서 사용하는 용도이므로, static class 나 다른 곳에서 property 값이 필요할 때 활용한다.
     *
     * @param key property key
     * @return string value
     * @author GEON LEE
     * @since 2023-08-08<br />
     */
    public static String getPropertyValue(String key) {
        return ApplicationContextHolder.getContext().getEnvironment().getProperty(key);
    }

    /**
     * Response writer 메서드
     *
     * @author GEONLEE
     * @since 2022-11-11<br />
     * 2024-03-29 setResponseWriter tokenResponse parameter 제거<br />
     */
    public static void setResponseWriter(
            ServletResponse response, String resultCode, String resultMsg) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(CommonVariables.GSON.toJson(ErrorResponse.builder()
                    .status(resultCode)
                    .message(resultMsg)
                    .build()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}