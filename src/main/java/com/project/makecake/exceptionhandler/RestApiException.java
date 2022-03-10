package com.project.makecake.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RestApiException {
    private int stateCode;
    private String state;
    private String message;
}
