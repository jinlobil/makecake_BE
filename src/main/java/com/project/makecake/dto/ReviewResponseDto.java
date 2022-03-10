package com.project.makecake.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewResponseDto {
    private Long reviewId;
    private String writerNickname;
    private String createdDate;
    private String content;
    private List<String> reviewImages;
}
