package com.project.makecake.dto.mypage;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MypageResponseDto {

    private String profileImg;
    private String nickname;
    private String email;

}
