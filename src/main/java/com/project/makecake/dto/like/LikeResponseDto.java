package com.project.makecake.dto.like;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeResponseDto {

    private boolean myLike;
    private int likeCnt;

}
