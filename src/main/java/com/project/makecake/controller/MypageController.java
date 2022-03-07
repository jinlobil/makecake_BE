package com.project.makecake.controller;

import com.project.makecake.dto.MypageResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    // 마이페이지 조회
    @GetMapping("/mypage")
    public MypageResponseDto getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.mypage(userDetails);
    }

    // 내가 그린 도안 조회

    // 내가 반응한 도안

    // 내가 반응한 매장

    // 내가 반응한 케이크
}
