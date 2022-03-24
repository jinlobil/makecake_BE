package com.project.makecake.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeResponseDto {
    private boolean myLike;
    private int likeCnt;
}
