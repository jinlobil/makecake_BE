package com.project.makecake.exceptionhandler;

import com.amazonaws.http.DefaultErrorResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketException;

// Global 예외처리
@RestControllerAdvice
public class RestApiExceptionHandler {

    // IllegalArgumentException 예외처리
    @ExceptionHandler(value = { IllegalArgumentException.class})
    public ResponseEntity<Object> handleApiRequestException(IllegalArgumentException ex) {
        RestApiException restApiException = new RestApiException(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase(),ex.getMessage());

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    // SocketException 예외처리
    @ExceptionHandler(value = {SocketException.class})
    public void handleSocketException(SocketException ex) {
        System.out.println("broken pipe가 발생했다!!!!!!!!!!!! 안돼!!!!!!!!!!!");
    }

}
