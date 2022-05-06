package com.project.makecake.dto.mypage;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyReviewResponseDto {

    private Long reviewId;
    private Long storeId;
    private String name;
    private String content;
    private String createdDate;
    private String reviewImages;

}
