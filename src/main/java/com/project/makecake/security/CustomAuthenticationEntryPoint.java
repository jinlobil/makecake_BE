package com.project.makecake.security;

import com.project.makecake.exceptionhandler.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");
        ErrorCode errorCode;
//        if (exception.equals(ErrorCode.LOGIN_ERROR.getCode())){
//            errorCode = ErrorCode.LOGIN_ERROR;
//            setResponse(response, errorCode);
//            return;
//        }
//        if (exception.equals(ErrorCode.EXPIRED_TOKEN.getCode())){
//            errorCode = ErrorCode.EXPIRED_TOKEN;
//            setResponse(response, errorCode);
//            return;
//        }
        if (exception.equals(ErrorCode.USER_NOT_FOUND.name())) {
            errorCode = ErrorCode.USER_NOT_FOUND;
            setResponse(response, errorCode);
            return;
        }
        if (exception.equals(ErrorCode.EXPIRED_JWT.name())) {
            errorCode = ErrorCode.EXPIRED_JWT;
            setResponse(response, errorCode);
            return;
        }
        if (exception.equals(ErrorCode.WRONG_JWT.name())) {
            errorCode = ErrorCode.WRONG_JWT;
            setResponse(response, errorCode);
            return;
        }
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charser=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println("{ \"message\" : \"" + errorCode.getMessage()
                + "\", \"code\" : \"" +  errorCode.name()
                + "\", \"status\" : " + errorCode.getHttpStatus()
                + ", \"errors\" : [ ] }");
    }
}
