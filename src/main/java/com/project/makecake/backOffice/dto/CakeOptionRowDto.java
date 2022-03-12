package com.project.makecake.backOffice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CakeOptionRowDto {
    private String mainCat;
    private String subCat;
    private String addedPrice;
    private String moreInfo;

    public CakeOptionRowDto(String mainCat, String subCat, String addedPrice, String moreInfo){
        this.mainCat = mainCat;
        this.subCat = subCat;
        this.addedPrice = addedPrice;
        this.moreInfo = moreInfo;
    }
}
