package com.project.makecake.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginCheckResponseDto {
    private Long userId;
    private String nickname;
}
