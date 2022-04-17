package com.project.makecake.dto.backoffice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CakeOptionRowDto {

    private String mainCat;
    private String subCat;
    private String priceState;
    private String addedPrice;
    private String moreInfo;

    public CakeOptionRowDto(String mainCat, String subCat, String priceState, String addedPrice, String moreInfo){
        this.mainCat = mainCat.trim();
        this.subCat = subCat.trim();
        this.priceState = priceState.trim();
        this.addedPrice = addedPrice.trim();
        this.moreInfo = moreInfo.trim();
    }

}
