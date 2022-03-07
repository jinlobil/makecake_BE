package com.project.makecake.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeReviewDto {
    private long reviewId;
    private String nickname;
    private String createdDate;
    private String content;
    private String storeNmae;
}
