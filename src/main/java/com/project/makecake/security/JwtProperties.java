package com.project.makecake.security;

public interface JwtProperties {
    // 시크릿 키
    String secretKey = "ourmakecakegoodluck";
    // 토큰 유효 시간 (60000밀리초 * 60분 * 24시간 * 3 = 3일)
    Long tokenValidTime = 10000L;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
