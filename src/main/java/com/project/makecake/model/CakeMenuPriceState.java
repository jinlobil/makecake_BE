package com.project.makecake.model;

import lombok.Getter;

@Getter
public enum CakeMenuPriceState {
    FIXED("FIXED"),
    UNFIXED("UNFIXED"),
    RANGE("RANGE");

    private String value;

    CakeMenuPriceState(String value){
        this.value = value;
    }
}
