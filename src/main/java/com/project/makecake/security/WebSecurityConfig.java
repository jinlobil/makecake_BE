package com.project.makecake.security;

import com.project.makecake.enums.UserRoleEnum;
import com.project.makecake.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
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

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // static 폴더 내부 정적자원들은 보안 필터를 적용하지 않음
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
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
//                // 로그인 요청 인증 X
//                .antMatchers(HttpMethod.POST, "/login").permitAll()
//                // 회원가입, 중복체크 등 인증 X
//                .antMatchers(HttpMethod.POST, "/user/**").permitAll()
//                // 로그인 체크 인증 X
//                .antMatchers(HttpMethod.GET, "/user/**").permitAll()
//                // /api로 된 POST 요청 인증 X
//                .antMatchers(HttpMethod.POST, "/api/**").permitAll()
//                // /api로 된 GET 요청 인증 X
//                .antMatchers(HttpMethod.GET,"/api/**").permitAll()
//                // 그 외 요청 모두 인증
//                .anyRequest().authenticated()
                .anyRequest().permitAll()
                .and().cors();

    }

    // CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader(HttpHeaders.AUTHORIZATION);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
