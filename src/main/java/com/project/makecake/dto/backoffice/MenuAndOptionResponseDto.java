package com.project.makecake.dto.backoffice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MenuAndOptionResponseDto {

    private long storeId;
    private String storeName;
    private List<CakeMenuRowDto> peekMenuList;
    private List<CakeOptionRowDto> peekOptionList;

    public MenuAndOptionResponseDto(
            long storeId,
            String storeName,
            List<CakeMenuRowDto> peekMenuList,
            List<CakeOptionRowDto> peekOptionList
    ){
        this.storeId = storeId;
        this.storeName = storeName;
        this.peekMenuList = peekMenuList;
        this.peekOptionList = peekOptionList;
    }

}
