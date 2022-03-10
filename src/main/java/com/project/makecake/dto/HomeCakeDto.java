package com.project.makecake.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeCakeDto {
    private Long cakeId;
    private String mainImg;
    private int likeCnt;

    public HomeCakeDto(Long cakeId, String mainImg, int likeCnt){
        this.cakeId = cakeId;
        this.mainImg = mainImg;
        this.likeCnt = likeCnt;
    }
}