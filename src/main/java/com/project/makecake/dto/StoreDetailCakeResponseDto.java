package com.project.makecake.dto;

import com.project.makecake.model.Cake;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreDetailCakeResponseDto {
    private Long cakeId;
    private String img;
    private int likeCnt;
    private Boolean myLike;

    public StoreDetailCakeResponseDto(Cake cake, boolean myLike){
        this.cakeId = cake.getCakeId();
        this.img = cake.getUrl();
        this.likeCnt = cake.getLikeCnt();
        this.myLike = myLike;
    }


}
