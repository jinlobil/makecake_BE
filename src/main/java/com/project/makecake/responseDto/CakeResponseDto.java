package com.project.makecake.responseDto;

import com.project.makecake.model.Cake;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CakeResponseDto {

    private Long cakeId;
    private String img;
    private String storeName;
    private int likeCnt;
    private boolean myLike;

    // 생성자
    public CakeResponseDto(Cake cake,boolean myLike) {
        this.cakeId = cake.getCakeId();
        this.img = cake.getUrl();
        this.storeName = cake.getStore().getName();
        this.likeCnt = cake.getLikeCnt();
        this.myLike = myLike;
    }
}
