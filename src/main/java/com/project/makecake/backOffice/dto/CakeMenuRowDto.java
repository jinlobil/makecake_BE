package com.project.makecake.backOffice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CakeMenuRowDto {
    private String type;
    private String size;
    private String price;
    private String moreInfo;

    public CakeMenuRowDto(String type, String size, String price, String moreInfo){
        this.type = type;
        this.size = size;
        this.price = price;
        this.moreInfo = moreInfo;
    }
}
