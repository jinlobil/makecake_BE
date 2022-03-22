package com.project.makecake.dto;

import com.project.makecake.model.Design;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DesignResponseDto {
    private Long designId;
    private String img;

    public DesignResponseDto(Design design) {
        this.designId = design.getDesignId();
        this.img = design.getImgUrl();
    }
}
