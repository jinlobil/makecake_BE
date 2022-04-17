package com.project.makecake.dto.backoffice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CakeMenuRowDto {

    private String type;
    private String size;
    private String priceState;
    private String price;
    private String moreInfo;

    public CakeMenuRowDto(String type, String size, String priceState, String price, String moreInfo){
        this.type = type;
        this.size = size;
        this.priceState = priceState;
        this.price = price;
        this.moreInfo = moreInfo;
    }

}
