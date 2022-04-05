package com.project.makecake.controller;

import com.project.makecake.dto.*;
import com.project.makecake.dto.noti.NewNotiResponseDto;
import com.project.makecake.dto.noti.NotiContentRequestDto;
import com.project.makecake.dto.noti.NotiRequestDto;
import com.project.makecake.dto.noti.NotiResponseDto;
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

    // 알림 내용 수정 API
    @PutMapping("/api/notis/{notiId}")
    public void editNoti(
            @PathVariable long notiId,
            @RequestBody NotiContentRequestDto requestDto
    ) {
        notiService.editNoti(notiId,requestDto);
    }

    // 고정 알림 추가 API
    @PostMapping("/api/notis/{notiId}/fix")
    public void addFixNoti(
            @PathVariable long notiId,
            @RequestBody RedirectUrlRequestDto requestDto
            ) {
        notiService.addFixNoti(notiId, requestDto);
    }

    // 고정 알림 reveal값 변경 API
    @PutMapping("/api/fixNotis/{fixNotiId}")
    public void editFixNoti(@PathVariable long fixNotiId) {
        notiService.editFixNoti(fixNotiId);
    }

    // 알림 발송 API
    @PostMapping("/api/notis/{notiId}/personal")
    public void addPersonalNoti(
            @PathVariable long notiId,
            @RequestBody RedirectUrlRequestDto requestDto
    ) {
        notiService.addPersonalNoti(notiId, requestDto);
    }

    // 새로운 알림 여부 조회 API
    @GetMapping("/home/new-noti")
    public NewNotiResponseDto getNewNoti(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notiService.getNewNoti(userDetails);
    }

    // 알림 조회 API
    @GetMapping("/noti")
    public NotiResponseDto getNotiList(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws ParseException {
        return notiService.getNotiList(userDetails);
    }
}
