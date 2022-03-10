package com.project.makecake.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreDetailCakeResponseDto {
    private Long cakeId;
    private String img;
    private int likeCnt;
    private Boolean myLike;
}
