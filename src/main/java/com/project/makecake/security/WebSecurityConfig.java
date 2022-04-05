package com.project.makecake.security;

import com.project.makecake.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.headers().frameOptions().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 시큐리티 폼로그인기능 비활성화
                .formLogin().disable()
                // 로그인폼 화면으로 리다이렉트 비활성화
                .httpBasic().disable()
                // UsernamePasswordAuthenticationFilter 단계에서 json로그인과 jwt토큰을 만들어 response 반환
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                // BasicAuthenticationFilter 단계에서 jwt토큰 검증
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
                .authorizeRequests()
                // PreFlight 요청 인증 X
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // 로그인 요청 인증 X
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                // 소셜로그인 요청 인증 X
                .antMatchers(HttpMethod.POST, "/user/**").permitAll()
                // 회원가입, 중복체크 등 인증 X
                .antMatchers(HttpMethod.POST, "/users/**").permitAll()
                // 로그인 체크 인증 X
                .antMatchers(HttpMethod.GET, "/users/**").permitAll()
                // nginx profile 요청 인증 X
                .antMatchers(HttpMethod.GET, "/profile").permitAll()
                // 메인화면 인증 X
                .antMatchers(HttpMethod.GET, "/home/**").permitAll()
                // 검색 인증 X
                .antMatchers(HttpMethod.GET, "/search/**").permitAll()
                // 매장 조회 인증 X
                .antMatchers(HttpMethod.GET, "/stores/**").permitAll()
                // 케이크 조회 인증 X
                .antMatchers(HttpMethod.GET, "/cakes/**").permitAll()
                // 게시물 조회 인증 X
                .antMatchers(HttpMethod.GET, "/posts/**").permitAll()
                // 주문 가이드 인증 X
                .antMatchers(HttpMethod.GET, "/order-guide").permitAll()
                // 주문가능한 매장 조회 인증 X
                .antMatchers(HttpMethod.GET, "/orders/stores").permitAll()
                // 그 외 요청 모두 인증
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
                .and().cors();
        // 시큐리티 예외처리
        http.addFilterBefore(exceptionHandlerFilter, JwtAuthorizationFilter.class);
        http.addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);

    }

    // CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://make-cake.com");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader(HttpHeaders.AUTHORIZATION);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
