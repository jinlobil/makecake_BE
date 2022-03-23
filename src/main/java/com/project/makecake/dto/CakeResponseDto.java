package com.project.makecake.dto;

import com.project.makecake.model.Cake;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CakeResponseDto {

    private Long cakeId, storeId;
    private String img, storeName;
    private int likeCnt;
    private boolean myLike;

    @Builder
    public CakeResponseDto(Cake cake, boolean myLike) {
        this.cakeId = cake.getCakeId();
        this.img = cake.getUrl();
        this.storeId = cake.getStore().getStoreId();
        this.storeName = cake.getStore().getName();
        this.likeCnt = cake.getLikeCnt();
        this.myLike = myLike;
    }
}
