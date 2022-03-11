package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MyDesignResponseDto {
    public Long postId;
    public Long designId;
    public String img;
}
