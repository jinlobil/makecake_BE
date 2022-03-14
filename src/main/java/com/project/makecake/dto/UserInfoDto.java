package com.project.makecake.dto;

import com.project.makecake.model.UserRoleEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserInfoDto {
    public Long userId;
    public String username;
    public String nickname;
    public String profileImgUrl;
    public String profileImgName;
    public String provider;
    public UserRoleEnum role;
    public String createdDate;
    public String modifiedDate;
}
