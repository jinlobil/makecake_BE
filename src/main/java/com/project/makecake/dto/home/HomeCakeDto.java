package com.project.makecake.dto.home;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class HomeCakeDto {
    private Long cakeId;
    private String mainImg;
    private int likeCnt;

    @Builder
    public HomeCakeDto(Long cakeId, String mainImg, int likeCnt){
        this.cakeId = cakeId;
        this.mainImg = mainImg;
        this.likeCnt = likeCnt;
    }
}