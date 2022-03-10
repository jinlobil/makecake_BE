package com.project.makecake.responseDto;

import com.project.makecake.model.Comment;
import com.project.makecake.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentResponseDto {
    private Long commentId;
    private String nickname;
    private String content;
    private String createdDate;

    // 생성자
    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.createdDate = comment.getCreatedAt();
    }
}
