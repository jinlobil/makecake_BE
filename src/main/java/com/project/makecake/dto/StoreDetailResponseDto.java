package com.project.makecake.dto;

import com.project.makecake.model.Store;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoreDetailResponseDto {
    private long storeId;
    private String mainImg;
    private String name;
    private String roadAddress;
    private String fullAddress;
    private String openTimeToday; //오늘 기준
    private String phone;
    private List<StoreDetailUrlDto> urls; //2개만
    private Boolean myLike;
    private int likeCnt;
    private List<StoreDetailMenuDto> menus; //수정 필요
    private List<StoreDetailCakeResponseDto> cakeImages;

    public StoreDetailResponseDto(Store store, String openTimeToday, List<StoreDetailUrlDto> urls,
                                  Boolean myLike,List<StoreDetailMenuDto> menus, List<StoreDetailCakeResponseDto> cakeImages){
        this.storeId = store.getStoreId();
        this.mainImg = store.getMainImg();
        this.name = store.getName();
        this.roadAddress = store.getRoadAddress();
        this.fullAddress = store.getFullAddress();
        this.openTimeToday = openTimeToday;
        this.phone = store.getPhone();
        this.urls = urls;
        this.myLike = myLike;
        this.menus = menus;
        this.cakeImages = cakeImages;
    }

}