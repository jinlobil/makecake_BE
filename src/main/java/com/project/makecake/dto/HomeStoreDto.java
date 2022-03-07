package com.project.makecake.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeStoreDto {
    private Long storeId;
    private String name;
    private String mainImg;
    private int likeCnt; //int vs long

    public HomeStoreDto(Long storeId, String name, String mainImg, int likeCnt){
        this.storeId = storeId;
        this.name = name;
        this.mainImg = mainImg;
        this.likeCnt = likeCnt;
    }


}
