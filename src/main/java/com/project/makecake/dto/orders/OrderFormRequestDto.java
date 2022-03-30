package com.project.makecake.dto.orders;

import lombok.Getter;
import lombok.Setter;

@Getter
public class OrderFormRequestDto {
    private long storeId;
    private String name;
    private String form;
    private String instruction;
}
