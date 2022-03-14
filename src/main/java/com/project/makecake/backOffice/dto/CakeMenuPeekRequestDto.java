package com.project.makecake.backOffice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CakeMenuPeekRequestDto {
    private long storeId;
    private String aboutCake;
    private String aboutOption;
}
