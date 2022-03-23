package com.project.makecake.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageInfoDto {
    private String url;
    private String name;
}
