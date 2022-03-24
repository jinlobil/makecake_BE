package com.project.makecake.dto;

import lombok.Getter;

@Getter
public class OrderFormRequestDto {
    private long storeId;
    private String form;
    private String instruction;
}
