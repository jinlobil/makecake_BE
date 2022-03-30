package com.project.makecake.dto.home;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeReviewDto {
    private long reviewId;
    private String nickname;
    private String createdDate;
    private String content;
    private String img;
    private long storeId;
    private String storeName;
}