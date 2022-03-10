package com.project.makecake.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    public String username;
    public String nickname;
    public String password;
    public String passwordCheck;
}
