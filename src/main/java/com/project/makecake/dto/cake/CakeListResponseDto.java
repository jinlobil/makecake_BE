package com.project.makecake.dto.cake;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CakeListResponseDto {

    private List<CakeSimpleResponseDto> cakeList;
    private boolean hasNext;

    @Builder
    public CakeListResponseDto(List<CakeSimpleResponseDto> dtoList, boolean hasNext) {
        this.cakeList = dtoList;
        this.hasNext = hasNext;
    }

}
