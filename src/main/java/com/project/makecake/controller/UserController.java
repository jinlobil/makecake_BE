package com.project.makecake.controller;

import com.project.makecake.dto.LoginCheckResponseDto;
import com.project.makecake.dto.SignupRequestDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/user/signup")
    public void registerUser(@RequestBody SignupRequestDto requestDto) {
        userService.registerUser(requestDto);
    }

    // username 중복검사
    @PostMapping("/user/usernameCheck")
    public HashMap<String, Boolean> usernameCheck(@RequestBody SignupRequestDto requestDto) {
        return userService.usernameCheck(requestDto);
    }

    // nickname 중복검사
    @PostMapping("/user/nicknameCheck")
    public HashMap<String, Boolean> nicknameCheck(@RequestBody SignupRequestDto requestDto) {
        return userService.nicknameCheck(requestDto);
    }

    // 로그인 체크
    @GetMapping("/user/loginCheck")
    public LoginCheckResponseDto loginCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.loginCheck(userDetails);
    }
}
