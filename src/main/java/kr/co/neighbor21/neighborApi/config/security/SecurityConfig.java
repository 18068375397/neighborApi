package kr.co.neighbor21.neighborApi.config.security;

import kr.co.neighbor21.neighborApi.common.variable.CommonVariables;
import kr.co.neighbor21.neighborApi.config.jwt.JwtAccessDeniedHandler;
import kr.co.neighbor21.neighborApi.config.jwt.JwtAuthenticationEntryPoint;
import kr.co.neighbor21.neighborApi.config.jwt.JwtFilter;
import kr.co.neighbor21.neighborApi.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author GEONLEE
 * @since 2023-01-11
 * 2023-02-09 LCH 테스트용 파일 다운로드 권한 설정.<br />
 * 2023-03-21 GEONLEE preview AllPermit 추가<br />
 * 2023-04-08 GEONLEE antMatchers request CmmnVar 에서 배열로 가져와서 처리<br />
 * 2023-04-10 GEONLEE 코드 정리<br />
 * 2023-05-11 GEONLEE 생성자 주입 방식으로 변경 (코드 검증 때문)<br />
 * 2023-08-18 GEONLEE WebSecurityCustomizer 주석처리, 이유는 메서드 주석 참고<br />
 * 2023-10-17 GEONLEE Springboot 3.x update 로 인한 변경 처리<br />
 * 2024-03-15 GEONLEE spring 6.2 부터 apply 메서드 deprecated 로 방식 변경<br />
 * 2024-03-28 GEONLEE - JwtFilter 파라미터 수정 messageConfig 제외<br />
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    private AuthFilter authFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 启用 cors 跨域
//                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(CommonVariables.IGNORE_URIS).permitAll()
                        .requestMatchers(CommonVariables.SWAGGER_URIS).permitAll()
                        .anyRequest().authenticated()).exceptionHandling(c ->
                        c.authenticationEntryPoint(jwtAuthenticationEntryPoint).accessDeniedHandler(jwtAccessDeniedHandler)
                // Spring Security does not adopt session mechanism
                ).sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Replace the default authentication filter with the custom authentication filter
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authFilter, FilterSecurityInterceptor.class);
//                .apply(new JwtSecurityConfig(tokenProvider, messageConfig)); /*spring 6.2 deprecated*/
        return http.build();
    }
}