package com.project.makecake.dto.store;

import com.project.makecake.model.Cake;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class StoreDetailCakeResponseDto {
    private Long cakeId;
    private String thumbnailImg;
    private int likeCnt;
    private Boolean myLike;

    @Builder
    public StoreDetailCakeResponseDto(Cake cake, boolean myLike){
        this.cakeId = cake.getCakeId();
        this.thumbnailImg = cake.getThumbnailUrl();
        this.likeCnt = cake.getLikeCnt();
        this.myLike = myLike;
    }


}
