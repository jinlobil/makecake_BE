package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MyReactPostResponceDto {
    public Long postId;
    public String img;
    public String nickname;
    public String profileImg;
    public String content;
    public String createdDate;
}
