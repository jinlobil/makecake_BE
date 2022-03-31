package com.project.makecake.dto.home;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class HomeCakeDto {
    private Long cakeId;
    private String thumbnailMainImg;
    private int likeCnt;

    @Builder
    public HomeCakeDto(Long cakeId, String thumbnailMainImg, int likeCnt){
        this.cakeId = cakeId;
        this.thumbnailMainImg = thumbnailMainImg;
        this.likeCnt = likeCnt;
    }
}