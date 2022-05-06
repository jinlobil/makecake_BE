package com.project.makecake.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotiType {

    LIKE(false, true),
    COMMENT(false, true),
    AD(true, false),
    NOTICE(true, false),
    BETA(true, false);

    private final boolean adminManage;
    private final boolean needMessageEdit;

}
