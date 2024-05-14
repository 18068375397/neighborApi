package kr.co.neighbor21.neighborApi.common.variable;

import com.google.gson.Gson;
import kr.co.neighbor21.neighborApi.common.util.CommonUtils;

/**
 * 공통 static class<br />
 * 공통으로 사용할 변수만 설정, method 는 commonUtils 로 이동<br />
 *
 * @author GEONLEE
 * @since 2022-11-07<br />
 * 2023-08-01 GEONLEE - 안쓰는 STATIC 정리<br />
 * 2023-10-17 GEONLEE - 클래스 명칭 변경 CmmnVar to CommonVariables<br />
 * 2024-03-29 GEONLEE - 대문자 변경 및 CONTEXT_PATH 추가<br />
 */
public class CommonVariables {
    /*SecurityConfig, JWTFilter 에서 사용*/
    public static final String[] IGNORE_URIS = {"/v1/login", "/v1/logout", "/v1/public-key", "/favicon.ico", "/error"};
    /*SecurityConfig 에서 사용*/
    public static final String[] SWAGGER_URIS = {"swagger-ui.html", "/swagger-ui/**", "/api-docs/**"};
    public static final String CONTEXT_PATH = CommonUtils.getPropertyValue("server.servlet.context-path");
    public static Gson GSON = new Gson();
}