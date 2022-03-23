package com.project.makecake.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotiType {
    LIKE(false, false, true),
    COMMENT(false, false, true),
    AD(true, true, false),
    NOTICE(true, true, false),
    BETA(true, true, false);

    private final boolean addAble;
    private final boolean fixAble;
    private final boolean messageEditAble;
}
