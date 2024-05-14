package kr.co.neighbor21.neighborApi.config.message;

import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * message/message.properties 파일을 읽어 메시지나 메시지 코드를 리턴해준다.
 *
 * @author GEONLEE
 * @since 2022-09-30<br />
 * 2023-04-10 GEONLEE 로거 메시지 수정<br />
 * 2023-05-18 GEONLEE 인스턴스 메소드에서 static 변수에 값 할당 문제 수정<br />
 */
@Configuration
public class MessageConfig {
    private final Properties msgProp;

    public MessageConfig() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream("message/message.properties")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(input), StandardCharsets.UTF_8));
            this.msgProp = new Properties();
            msgProp.load(reader);
        }
    }

    /**
     * 메시지 키를 전달받아 메시지를 리턴
     */
    public String getMsg(String key) {
        return msgProp.getProperty(key);
    }

    /**
     * 코드 키를 전달 받아 코드를 리턴
     */
    public String getCode(String key) {
        return msgProp.getProperty(key);
    }
}