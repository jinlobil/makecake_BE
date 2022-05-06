package com.project.makecake.controller.backoffice;

import com.project.makecake.dto.RedirectUrlRequestDto;
import com.project.makecake.dto.noti.NotiContentRequestDto;
import com.project.makecake.dto.noti.NotiRequestDto;
import com.project.makecake.service.backoffice.BONotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BONotiController {

    private final BONotiService BONotiService;

    // 알림 등록 API
    @Secured("ROLE_ADMIN")
    @PostMapping("/back-office/noti")
    public void addNoti(@RequestBody NotiRequestDto requestDto) {
        BONotiService.addNoti(requestDto);
    }

    // 알림 내용 수정 API
    @Secured("ROLE_ADMIN")
    @PutMapping("/back-office/notis/{notiId}")
    public void editNoti(
            @PathVariable long notiId,
            @RequestBody NotiContentRequestDto requestDto
    ) {
        BONotiService.editNoti(notiId,requestDto);
    }

    // 고정 알림 추가 API
    @Secured("ROLE_ADMIN")
    @PostMapping("/back-office/notis/{notiId}/fix")
    public void addFixNoti(
            @PathVariable long notiId,
            @RequestBody RedirectUrlRequestDto requestDto
    ) {
        BONotiService.addFixNoti(notiId, requestDto);
    }

    // 고정 알림 reveal값 변경 API
    @Secured("ROLE_ADMIN")
    @PutMapping("/back-office/fixNotis/{fixNotiId}")
    public void editFixNoti(@PathVariable long fixNotiId) {
        BONotiService.editFixNoti(fixNotiId);
    }

    // 알림 발송 API
    @Secured("ROLE_ADMIN")
    @PostMapping("/back-office/notis/{notiId}/personal")
    public void addPersonalNoti(
            @PathVariable long notiId,
            @RequestBody RedirectUrlRequestDto requestDto
    ) {
        BONotiService.addPersonalNoti(notiId, requestDto);
    }

}
