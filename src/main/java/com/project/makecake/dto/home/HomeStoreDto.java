package com.project.makecake.dto.home;

import lombok.Builder;
import lombok.Getter;

@Getter
public class HomeStoreDto {

    private Long storeId;
    private String name;
    private String mainImg;
    private int likeCnt;

    @Builder
    public HomeStoreDto(Long storeId, String name, String mainImg, int likeCnt){
        this.storeId = storeId;
        this.name = name;
        this.mainImg = mainImg;
        this.likeCnt = likeCnt;
    }

}