package com.project.makecake.security;

import com.project.makecake.exceptionhandler.CustomException;
import com.project.makecake.exceptionhandler.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 필터체인중에 CustomException을 캐치
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            ErrorCode errorCode = e.getErrorCode();
            log.error("error response : " + errorCode.name());
            setErrorResponse(response, errorCode);
        }
    }

    // 반환할 response 작성
    public void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println("{ \"status\" : " + errorCode.getHttpStatus().value()
                + ", \"error\" : \"" + errorCode.getHttpStatus().name()
                + "\", \"code\" : \"" + errorCode.name()
                + "\", \"message\" : \"" + errorCode.getMessage()
                + "\" }");
    }
}
