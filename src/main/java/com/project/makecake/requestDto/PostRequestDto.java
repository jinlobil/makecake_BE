package com.project.makecake.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String content;
    private String size;
    private String shape;
    private String purpose;
    private boolean isMade;
    private Long storeId;
}
