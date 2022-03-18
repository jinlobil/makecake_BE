package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MyReviewResponseDto {
    public Long reviewId;
    public Long storeId;
    public String name;
    public String content;
    public String createdDate;
    public String reviewImages;
}
