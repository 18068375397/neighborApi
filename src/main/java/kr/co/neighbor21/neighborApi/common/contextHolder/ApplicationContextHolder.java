package kr.co.neighbor21.neighborApi.common.contextHolder;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean 에 등록 전 객체를 활용할 수 있는 메서드<br />
 * Spring bean 이 null 일 경우 활용<br />
 * UserService userService = ApplicationContextHolder.getContext().getBean(UserService.class);<br />
 *
 * @author GEON LEE
 * @since 2018-06-04
 * 2024-03-15 GEONLEE - @NotNull 추가
 **/
@Configuration
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}