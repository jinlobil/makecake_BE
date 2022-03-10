package com.project.makecake.dto;

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
    private String description;
    private List<StoreDetailUrlDto> urls;
    private String openTimeString;
    //private OpenTimeDto openTime;
    private Boolean myLike;
    private int likeCnt;
    private List<StoreDetailMenuDto> menus;
    private List<ReviewResponseDto> reviews;
    private List<StoreDetailCakeResponseDto> cakeImages;
}
