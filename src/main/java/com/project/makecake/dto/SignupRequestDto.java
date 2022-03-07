package com.project.makecake.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String username;
    private String nickname;
    private String password;
}
