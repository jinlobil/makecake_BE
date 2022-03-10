package com.project.makecake.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreDetailMenuDto {
    private String name;
    private String price;
    private Boolean changes;
}
