package com.project.makecake.dto;

import com.project.makecake.model.OrderForm;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderReadyStoreResponseDto {
    private long storeId;
    private String name, simpleAddress;

    @Builder
    public OrderReadyStoreResponseDto(OrderForm orderForm, String simpleAddress){
        this.storeId = orderForm.getStore().getStoreId();
        this.name = orderForm.getStore().getName();
        this.simpleAddress = simpleAddress;
    }
}
