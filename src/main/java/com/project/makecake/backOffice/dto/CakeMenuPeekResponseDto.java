package com.project.makecake.backOffice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CakeMenuPeekResponseDto {
    private long storeId;
    private String storeName;
    private List<CakeMenuRowDto> peekMenuList;
    private List<CakeOptionRowDto> peekOptionList;

    public CakeMenuPeekResponseDto(long storeId, String storeName, List<CakeMenuRowDto> peekMenuList, List<CakeOptionRowDto> peekOptionList){
        this.storeId = storeId;
        this.storeName = storeName;
        this.peekMenuList = peekMenuList;
        this.peekOptionList = peekOptionList;
    }
}
