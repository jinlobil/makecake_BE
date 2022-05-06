package com.project.makecake.dto.orders;

import lombok.Getter;

import java.util.List;

@Getter
public class UserOrderRequestDto {

    private long designId;
    private List<String> userInput;

}
