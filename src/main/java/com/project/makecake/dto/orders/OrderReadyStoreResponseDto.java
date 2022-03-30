package com.project.makecake.dto.orders;

import com.project.makecake.model.OrderForm;
import com.project.makecake.model.Store;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderReadyStoreResponseDto {
    private long storeId;
    private String name, simpleAddress;

    @Builder
//    public OrderReadyStoreResponseDto(OrderForm orderForm, String simpleAddress){
//        this.storeId = orderForm.getStore().getStoreId();
//        this.name = orderForm.getStore().getName();
//        this.simpleAddress = simpleAddress;
//    }
    public OrderReadyStoreResponseDto(Store store, String simpleAddress){
        this.storeId = store.getStoreId();
        this.name = store.getName();
        this.simpleAddress = simpleAddress;
    }
}
