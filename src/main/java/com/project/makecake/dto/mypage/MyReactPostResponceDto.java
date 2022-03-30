package com.project.makecake.dto.mypage;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyReactPostResponceDto {
    private Long postId;
    private String img;
    private String nickname;
    private String profileImg;
    private String content;
    private String createdDate;
}
