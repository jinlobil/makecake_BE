package com.project.makecake.controller.backoffice;

import com.project.makecake.dto.RedirectUrlRequestDto;
import com.project.makecake.dto.noti.NotiContentRequestDto;
import com.project.makecake.dto.noti.NotiRequestDto;
import com.project.makecake.service.backoffice.AdminNotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminNotiController {

    private final AdminNotiService adminNotiService;

    // 알림 등록 API
    @PostMapping("/api/noti")
    public void addNoti(@RequestBody NotiRequestDto requestDto) {
        adminNotiService.addNoti(requestDto);
    }

    // 알림 내용 수정 API
    @PutMapping("/api/notis/{notiId}")
    public void editNoti(
            @PathVariable long notiId,
            @RequestBody NotiContentRequestDto requestDto
    ) {
        adminNotiService.editNoti(notiId,requestDto);
    }

    // 고정 알림 추가 API
    @PostMapping("/api/notis/{notiId}/fix")
    public void addFixNoti(
            @PathVariable long notiId,
            @RequestBody RedirectUrlRequestDto requestDto
    ) {
        adminNotiService.addFixNoti(notiId, requestDto);
    }

    // 고정 알림 reveal값 변경 API
    @PutMapping("/api/fixNotis/{fixNotiId}")
    public void editFixNoti(@PathVariable long fixNotiId) {
        adminNotiService.editFixNoti(fixNotiId);
    }

    // 알림 발송 API
    @PostMapping("/api/notis/{notiId}/personal")
    public void addPersonalNoti(
            @PathVariable long notiId,
            @RequestBody RedirectUrlRequestDto requestDto
    ) {
        adminNotiService.addPersonalNoti(notiId, requestDto);
    }
}
