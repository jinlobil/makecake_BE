package com.project.makecake.dto;

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
//    private String addressSimple;
    private String roadAddress;
    private String fulAddress;
    private String mainImg;
    private int likeCnt;
    private int reviewCnt;

}
