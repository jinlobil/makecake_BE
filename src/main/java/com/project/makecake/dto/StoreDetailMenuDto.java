package com.project.makecake.dto;

import com.project.makecake.model.CakeMenu;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class StoreDetailMenuDto {
    private String type;
    private String size;
    private String price;

    public StoreDetailMenuDto(CakeMenu cakeMenu){
        this.type = cakeMenu.getType();
        this.size = cakeMenu.getSize();
        this.price = cakeMenu.getPrice();
    }
}
