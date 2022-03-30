package com.project.makecake.dto.mypage;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyCommentResponseDto {
    private Long commentId;
    private String content;
    private String createdDate;
    private Long postId;
    private String postTitle;
}
