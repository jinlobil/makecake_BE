
package com.project.makecake.dto.home;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Setter
@Getter
public class HomeResponseDto {

    private List<HomeStoreDto> homeStoreDtoList;
    private List<HomeCakeDto> homeCakeDtoList;

    public HomeResponseDto(List<HomeStoreDto> homeStoreDtoList, List<HomeCakeDto> homeCakeDtoList){
        this.homeStoreDtoList = homeStoreDtoList;
        this.homeCakeDtoList = homeCakeDtoList;
    }

}