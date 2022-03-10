package com.project.makecake.controller;

import com.project.makecake.dto.LoginCheckResponseDto;
import com.project.makecake.dto.SignupRequestDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/user/signup")
    public HashMap<String, Boolean> registerUser(@RequestBody SignupRequestDto requestDto) {
        return userService.registerUser(requestDto);
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

    // 프로필 수정
    @PutMapping("/profile")
    public void editProfile(){

    }

    // 회원탈퇴
    @PutMapping("/user/resign")
    public void resignUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.resignUser(userDetails);
    }
}
