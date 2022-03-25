package com.project.makecake.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UserOrderRequestDto {
    private long designId;
    private List<String> userInput;
}
