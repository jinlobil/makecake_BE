package com.project.makecake.responseDto;

import com.project.makecake.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostSimpleResponseDto {

    private Long postId;
    private String img;

    public PostSimpleResponseDto(Post post) {
        this.postId = post.getPostId();
        this.img = post.getDesign().getImgUrl();
    }
}
