package com.project.makecake.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotiType {
    LIKE(false, false),
    COMMENT(false, false),
    AD(true, true),
    NOTICE(true, true),
    BETA(true, true);

    private final boolean addAble;
    private final boolean fixAble;
}
