package com.project.makecake.dto.orders;

import lombok.Getter;

@Getter
public class OrderFormRequestDto {

    private long storeId;
    private String name;
    private String form;
    private String instruction;

}
