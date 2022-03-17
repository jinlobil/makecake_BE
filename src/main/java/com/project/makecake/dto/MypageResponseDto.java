package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MypageResponseDto {
    public String profileImg;
    public String nickname;
    public String email;
}
