package com.example.bank.config;

import com.example.bank.config.jwt.JwtAuthenticationFilter;
import com.example.bank.config.jwt.JwtAuthorizationFilter;
import com.example.bank.domain.user.UserEnum;
import com.example.bank.dto.ResponseDto;
import com.example.bank.util.CustomResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class SecurityConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Bean //Ioc 컨테이너에 BCryptPasswordEncoder() 객체가 등록됨
    public BCryptPasswordEncoder passwordEncoder(){
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨");
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록이 필요함.
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity>{
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
            super.configure(builder);
        }
    }

    // JWT 서버를 만들 예정! Session 사용안함.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.debug("디버그 : filterChain 빈 등록됨");
        http.headers().frameOptions().disable(); //iframe 허용안함.
        http.csrf().disable(); // enable이면 post맨 작동안함
        http.cors().configurationSource(configurationSource());

        // jSessionId를 서버쪽에서 관리안함
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //react, 앱으로 요청할 예정
        http.formLogin().disable();
        //httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.
        http.httpBasic().disable();

        // JWT 필터 적용
        http.apply(new CustomSecurityFilterManager());

        // 인증 실패(exception 가로채기)
        http.exceptionHandling().authenticationEntryPoint((request, response, authException)->{
            CustomResponseUtil.fail(response,"로그인을 진행해 주세요", HttpStatus.UNAUTHORIZED); //401
        });

        // 권한 실패
        http.exceptionHandling().accessDeniedHandler((request, response, e)->{
            CustomResponseUtil.fail(response,"권한이 없습니다", HttpStatus.FORBIDDEN);
        });

        http.authorizeRequests()
                .antMatchers("/api/s/**").authenticated()
                .antMatchers("/api/admin/**").hasRole(""+UserEnum.ADMIN) //최근 공식문서에서는 ROLE_ 안붙여도 됨
                .anyRequest().permitAll();

        return http.build();
    }

    public CorsConfigurationSource configurationSource(){
        log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*"); //모든 헤더를 허용
        configuration.addAllowedMethod("*"); //GET,POST,PUT,DELETE
        configuration.addAllowedOriginPattern("*"); //모든 IP 주소 허용(프론트쪽 IP만 허용)
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization"); // 옛날에는 디폴트 였으나 현재는 추가해줘야함.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        //모든 주소 요청에 대해서 configuration을 전달
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
