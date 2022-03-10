package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MyReactCakeResponseDto {
    public Long cakeId;
    public String img;
    public String storeName;
    public int likeCnt;
    public Boolean myLike;
}
