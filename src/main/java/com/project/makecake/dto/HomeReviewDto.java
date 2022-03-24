package com.project.makecake.dto;

import lombok.Builder;
import lombok.Setter;

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