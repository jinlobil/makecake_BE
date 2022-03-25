package com.project.makecake.dto.backoffice;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderFormPeekResponseDto {
    private long storeId;
    private String storeName;
    private String name;
    private List<String> peekFormList;
    private List<String> peekInstructionList;

    @Builder
    public OrderFormPeekResponseDto(long storeId, String storeName, String name, List<String> peekFormList, List<String> peekInstructionList){
        this.storeId = storeId;
        this.storeName = storeName;
        this.name = name;
        this.peekFormList = peekFormList;
        this.peekInstructionList = peekInstructionList;
    }
}
