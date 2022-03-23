package com.project.makecake.controller;

import com.project.makecake.dto.NotiRequestDto;
import com.project.makecake.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotiController {

    private final NotiService notiService;

    // 알림 등록 API
    @PostMapping("/api/noti")
    public void addNoti(@RequestBody NotiRequestDto requestDto) {
        notiService.addNoti(requestDto);
    }
}
