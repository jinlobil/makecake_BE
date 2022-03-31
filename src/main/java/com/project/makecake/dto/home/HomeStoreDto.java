package com.project.makecake.dto.home;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class HomeStoreDto {
    private Long storeId;
    private String name;
    private String thumbnailMainImg;
    private int likeCnt; //int vs long

    @Builder
    public HomeStoreDto(Long storeId, String name, String thumbnailMainImg, int likeCnt){
        this.storeId = storeId;
        this.name = name;
        this.thumbnailMainImg = thumbnailMainImg;
        this.likeCnt = likeCnt;
    }


}