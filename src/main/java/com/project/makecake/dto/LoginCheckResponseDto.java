package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginCheckResponseDto {
    public Long userId;
    public String nickname;
}
