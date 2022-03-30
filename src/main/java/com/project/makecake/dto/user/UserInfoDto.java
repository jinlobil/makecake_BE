package com.project.makecake.dto.user;

import com.project.makecake.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoDto {
    private Long userId;
    private String username;
    private String nickname;
    private String profileImgUrl;
    private String profileImgName;
    private String provider;
    private UserRoleEnum role;
    private String createdDate;
    private String modifiedDate;
}
