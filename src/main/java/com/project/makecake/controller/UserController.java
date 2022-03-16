package com.project.makecake.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.dto.LoginCheckResponseDto;
import com.project.makecake.dto.MypageResponseDto;
import com.project.makecake.dto.SignupRequestDto;
import com.project.makecake.model.FolderName;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.S3UploadService;
import com.project.makecake.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final S3UploadService s3UploadService;

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
        return userService.loginChecked(userDetails);
    }

    // 프로필이미지 수정
    @PutMapping("/user/editProfile")
    public MypageResponseDto editProfile(@RequestParam(value = "img", required = false) MultipartFile multipartFile,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.editProfile(multipartFile, userDetails);
    }

    // 닉네임 수정
    @PutMapping("/user/editNickname")
    public MypageResponseDto editNickname(@RequestBody SignupRequestDto signupRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.editNickname(signupRequestDto, userDetails);
    }

    // 회원탈퇴
    @PutMapping("/user/resign")
    public MypageResponseDto resignUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.resignUser(userDetails);
    }

    // (임시) 이미지 업로드
    @PostMapping("/user/image")
    public ImageInfoDto userImage(@RequestParam(value = "imageFile", required = false) MultipartFile multipartFile) throws IOException {
        ImageInfoDto imageInfoDto = s3UploadService.uploadFile(multipartFile, FolderName.PROFILE.name());
        return imageInfoDto;
    }

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        userService.kakaoLogin(code, response);
    }

    // 네이버 로그인
    @GetMapping("/user/naver/callback")
    public void naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
        userService.naverLogin(code, state, response);
    }

    // 구글 로그인
    @GetMapping("/user/google/callback")
    public void googleLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        userService.google(code, response);
    }
}
