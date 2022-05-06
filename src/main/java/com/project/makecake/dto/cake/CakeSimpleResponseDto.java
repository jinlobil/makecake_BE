package com.project.makecake.dto.cake;

import com.project.makecake.model.Cake;
import lombok.Getter;

@Getter
public class CakeSimpleResponseDto {

    Long cakeId;
    String img;
    int likeCnt;

    public CakeSimpleResponseDto(Cake cake) {
        this.cakeId = cake.getCakeId();
        this.img = cake.getThumbnailUrl();
        this.likeCnt = cake.getLikeCnt();
    }
}
