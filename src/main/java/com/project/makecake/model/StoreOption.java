package com.project.makecake.model;

import com.project.makecake.backOffice.dto.CakeOptionRowDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class StoreOption extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long storeOptionId;

    @Column
    private String mainCat;

    @Column
    private String subCat;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "CakePriceState default FIXED")
    private CakePriceState priceState;

    @Column(columnDefinition = "integer default 0")
    private String addedPrice;

    @Column
    private String moreInfo;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;

    public StoreOption(CakeOptionRowDto optionRowDto, Store store, CakePriceState priceState){
        this.mainCat = optionRowDto.getMainCat();
        this.subCat = optionRowDto.getSubCat();
        this.addedPrice = optionRowDto.getAddedPrice();
        this.moreInfo = optionRowDto.getMoreInfo();
        this.priceState = priceState;
        this.store = store;
    }

}
