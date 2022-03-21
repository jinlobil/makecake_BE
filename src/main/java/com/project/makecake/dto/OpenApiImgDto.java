package com.project.makecake.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenApiImgDto {
    private String storeName;
    private List<String> imageList;
}
