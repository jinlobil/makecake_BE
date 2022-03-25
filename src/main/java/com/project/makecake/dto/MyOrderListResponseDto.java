package com.project.makecake.dto;

import lombok.Builder;

@Builder
public class MyOrderListResponseDto {
    private Long userOrdersId;
    private Long designId;
    private String img;
}
