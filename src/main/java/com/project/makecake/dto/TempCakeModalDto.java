package com.project.makecake.dto;

import com.project.makecake.model.Cake;
import com.project.makecake.responseDto.CakeResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class TempCakeModalDto {
    private Long cakeId;
    private String img;
    private String storeName;
    private int likeCnt;
    private boolean myLike;
    private Long storeId;

    // 생성자
    public TempCakeModalDto(CakeResponseDto responseDto, Long storeId) {
        this.cakeId = responseDto.getCakeId();
        this.img = responseDto.getImg();
        this.storeName = responseDto.getStoreName();
        this.likeCnt = responseDto.getLikeCnt();
        this.myLike = responseDto.isMyLike();
        this.storeId = storeId;
    }
}
