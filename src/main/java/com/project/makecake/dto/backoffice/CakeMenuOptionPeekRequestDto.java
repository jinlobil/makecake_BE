package com.project.makecake.dto.backoffice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CakeMenuOptionPeekRequestDto {
    private long storeId;
    private String aboutCake;
    private String aboutOption;
}
