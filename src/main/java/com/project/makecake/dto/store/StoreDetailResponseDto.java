package com.project.makecake.dto.store;

import com.project.makecake.model.Store;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StoreDetailResponseDto {
    private long storeId;
    private String mainImg;
    private String name;
    private String roadAddress;
    private String fullAddress;
    private OpenTimeResponseDto openTimeToday; //오늘 기준
    private String phone;
    private List<StoreDetailUrlDto> urlList; //2개만
    private Boolean myLike;
    private int likeCnt;
    private List<StoreDetailMenuDto> menuList; //수정 필요
    private StoreMoreDetailsDto moreDetails;
    private List<StoreDetailCakeResponseDto> cakeImgList;

    @Builder
    public StoreDetailResponseDto(Store store, OpenTimeResponseDto openTimeToday, List<StoreDetailUrlDto> urlList,
                                  Boolean myLike,int likeCnt, List<StoreDetailMenuDto> menuList, StoreMoreDetailsDto moreDetails, List<StoreDetailCakeResponseDto> cakeImgList){
        this.storeId = store.getStoreId();
        this.mainImg = store.getMainImg();
        this.name = store.getName();
        this.roadAddress = store.getRoadAddress();
        this.fullAddress = store.getFullAddress();
        this.openTimeToday = openTimeToday;
        this.phone = store.getPhone();
        this.urlList = urlList;
        this.myLike = myLike;
        this.likeCnt = likeCnt;
        this.menuList = menuList;
        this.moreDetails = moreDetails;
        this.cakeImgList = cakeImgList;
    }

}