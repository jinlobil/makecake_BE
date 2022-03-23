package com.project.makecake.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MypageResponseDto {
    private String profileImg;
    private String nickname;
    private String email;
}
