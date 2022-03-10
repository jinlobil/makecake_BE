package com.project.makecake.controller;

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
    public void resignUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.resignUser(userDetails);
    }

    // (임시) 이미지 업로드
    @PostMapping("/user/image")
    public ImageInfoDto userImage(@RequestParam(value = "imageFile", required = false) MultipartFile multipartFile) throws IOException {
        ImageInfoDto imageInfoDto = s3UploadService.uploadFile(multipartFile, FolderName.PROFILE.name());
        return imageInfoDto;
    }
}
