package com.project.makecake.dto.mypage;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyDesignResponseDto {
    private Long postId;
    private Long designId;
    private String img;
}
