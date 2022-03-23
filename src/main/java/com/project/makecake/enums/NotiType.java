package com.project.makecake.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotiType {
    LIKE(false),
    COMMENT(false),
    AD(true),
    NOTICE(true),
    BETA(true);

    private final boolean addAble;
}
