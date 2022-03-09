package com.project.makecake.responseDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PostDetailResponseDto {
    private String title;
    private Long designId;
    private String img;
    private String nickname;
    private int likeCnt;
    private boolean myLike;
    private String createdDate;
    private String content;
    private String size;
    private String shape;
    private String purpose;
    private boolean isMade;
    private String storeName;
}
