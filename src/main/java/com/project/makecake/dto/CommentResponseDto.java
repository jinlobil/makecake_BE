package com.project.makecake.dto;

import com.project.makecake.model.Comment;
import lombok.Getter;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private String nickname, content, createdDate;

    // 생성자
    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.createdDate = comment.getCreatedAt();
    }
}
