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

    @Column
    private String addedPrice;

    @Column
    private String moreInfo;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;

    public StoreOption(CakeOptionRowDto optionRowDto, Store store){
        this.mainCat = optionRowDto.getMainCat();
        this.subCat = optionRowDto.getSubCat();
        this.addedPrice = optionRowDto.getAddedPrice();
        this.moreInfo = optionRowDto.getMoreInfo();
        this.store = store;
    }

}
