package kr.co.neighbor21.neighborApi;

import kr.co.neighbor21.neighborApi.common.jpa.querydsl.repository.JpaDynamicRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaRepositories(repositoryBaseClass = JpaDynamicRepositoryImpl.class)
@EnableJpaAuditing
public class NeighborApiApplication extends SpringBootServletInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(NeighborApiApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(NeighborApiApplication.class);
    }

    /**
     * application.yml 설정 파일을 읽어 Swagger 정보 출력
     */
    @Component
    public class SwaggerInformation implements ApplicationRunner {
        @Value("${server.port}")
        private String port;
        @Value("${server.servlet.context-path}")
        private String contextPath;

        @Override
        public void run(ApplicationArguments args) {
            LOGGER.info("Swagger : http://localhost:" + port + contextPath + "/swagger-ui/index.html");
        }
    }
}
