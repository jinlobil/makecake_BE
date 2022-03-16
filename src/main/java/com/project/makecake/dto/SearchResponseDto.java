package com.project.makecake.dto;

import com.project.makecake.model.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

public class SearchResponseDto {
    private long storeId;
    private String name;
    private float x;
    private float y;
    private String addressSimple;
    private String roadAddress;
    private String fullAddress;
    private String mainImg;
    private int likeCnt;
    private int reviewCnt;

    public SearchResponseDto(Store store, String addressSimple){
        this.storeId = store.getStoreId();
        this.name = store.getName();
        this.x = store.getX();
        this.y = store.getY();
        this.addressSimple = addressSimple;
        this.roadAddress = store.getRoadAddress();
        this.fullAddress = store.getFullAddress();
        this.mainImg = store.getMainImg();
        this.likeCnt = store.getLikeCnt();
        this.reviewCnt = store.getReviewCnt();
    }
}
