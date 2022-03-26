package com.project.makecake.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyReactCakeResponseDto {
    private Long cakeId;
    private String img;
    private String storeName;
    private Long storeId;
    private int likeCnt;
    private Boolean myLike;
}
