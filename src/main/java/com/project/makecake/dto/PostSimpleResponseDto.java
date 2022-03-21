package com.project.makecake.dto;

import com.project.makecake.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostSimpleResponseDto {

    private Long postId;
    private String img;
    private boolean myLike;

    public PostSimpleResponseDto(Post post, boolean myLike) {
        this.postId = post.getPostId();
        this.img = post.getDesign().getImgUrl();
        this.myLike = myLike;
    }
}
