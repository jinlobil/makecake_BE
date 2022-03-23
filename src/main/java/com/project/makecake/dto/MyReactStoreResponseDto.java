package com.project.makecake.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyReactStoreResponseDto {
    private Long storeId;
    private String name;
    private String addressSimple;
    private String mainImg;
}
