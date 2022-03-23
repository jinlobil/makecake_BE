package com.project.makecake.controller;

import com.project.makecake.dto.NotiRequestDto;
import com.project.makecake.dto.NotiResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RequiredArgsConstructor
@RestController
public class NotiController {

    private final NotiService notiService;

    // 알림 등록 API
    @PostMapping("/api/noti")
    public void addNoti(@RequestBody NotiRequestDto requestDto) {
        notiService.addNoti(requestDto);
    }

    // 고정 알림 추가 API
    @PostMapping("/api/notis/{notiId}/fix")
    public void addFixNoti(@PathVariable long notiId) {
        notiService.addFixNoti(notiId);
    }

    // 고정 알림 reveal값 변경 API
    @PutMapping("/api/fixNotis/{fixNotiId}")
    public void editFixNoti(@PathVariable long fixNotiId) {
        notiService.editFixNoti(fixNotiId);
    }

    // 알림 조회 API
    @GetMapping("/noti")
    public NotiResponseDto getNotiList(@AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
        NotiResponseDto responseDto = notiService.getNotiList(userDetails);
        return responseDto;
    }
}
