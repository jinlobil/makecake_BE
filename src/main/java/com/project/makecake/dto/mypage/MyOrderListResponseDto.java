package com.project.makecake.dto.mypage;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyOrderListResponseDto {
    private Long userOrdersId;
    private Long designId;
    private String img;
}
