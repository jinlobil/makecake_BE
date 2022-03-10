package com.project.makecake.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MyReactStoreResponseDto {
    public Long storeId;
    public String name;
    public String addressSimple;
    public String mainImg;
}
