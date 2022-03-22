package com.project.makecake.dto.backoffice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CakeMenuOptionPeekResponseDto {
    private long storeId;
    private String storeName;
    private List<CakeMenuRowDto> peekMenuList;
    private List<CakeOptionRowDto> peekOptionList;

    public CakeMenuOptionPeekResponseDto(long storeId, String storeName, List<CakeMenuRowDto> peekMenuList, List<CakeOptionRowDto> peekOptionList){
        this.storeId = storeId;
        this.storeName = storeName;
        this.peekMenuList = peekMenuList;
        this.peekOptionList = peekOptionList;
    }
}
