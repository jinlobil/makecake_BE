package com.project.makecake.dto;

import com.project.makecake.model.CakeMenu;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreMoreDetailsDto {
    private List<StoreMoreCakeMenuDto> cakeMenuList;
    private List<StoreMoreCakeTasteDto> cakeTasteList;
    private List<StoreMoreCakeOptionDto> cakeOptionList;
}
