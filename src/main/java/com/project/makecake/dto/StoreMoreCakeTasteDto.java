package com.project.makecake.dto;

import com.project.makecake.model.StoreOption;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StoreMoreCakeTasteDto {
    private String flavor;
    private String addedPrice;
    private String moreInfo;

    @Builder
    public StoreMoreCakeTasteDto (StoreOption storeOption){
        this.flavor = storeOption.getSubCat();
        this.addedPrice = storeOption.getAddedPrice();
        this.moreInfo = storeOption.getMoreInfo();
    }
}
