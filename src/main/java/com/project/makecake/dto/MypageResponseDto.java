package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MypageResponseDto {
    public String userPicture;
    public String nickname;
    public String provider;
    public String email;
}
