package com.project.makecake.dto;

import com.project.makecake.model.Review;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewResponseTempDto {
    private Long reviewId;
    private String writerNickname;
    private String createdDate;
    private String content;
    private String reviewImage;

    public ReviewResponseTempDto(Review review, String reviewImage){
        this.reviewId = review.getReviewId();
        this.writerNickname = review.getUser().getNickname();
        this.createdDate = review.getCreatedAt();
        this.content = review.getContent();
        this.reviewImage = reviewImage;
    }
}
