package com.project.makecake.exceptionhandler;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
public class ErrorResponse {
    private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public static ResponseEntity<ErrorResponse> responseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .build()
                );
    }
}
