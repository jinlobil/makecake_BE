package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MyCommentResponseDto {
    public Long commentId;
    public String content;
    public String createdDate;
    public Long postId;
    public String postTitle;
    public String writerNickname;
}
