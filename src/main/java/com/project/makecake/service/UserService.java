package com.project.makecake.service;

import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.dto.LoginCheckResponseDto;
import com.project.makecake.dto.MypageResponseDto;
import com.project.makecake.dto.SignupRequestDto;
import com.project.makecake.model.FolderName;
import com.project.makecake.model.User;
import com.project.makecake.model.UserRoleEnum;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    // 회원가입
    public HashMap<String, Boolean> registerUser(SignupRequestDto requestDto) {

        String username = requestDto.getUsername();

        String nickname = requestDto.getNickname();

        String password = passwordEncoder.encode(requestDto.getPassword());

        String profileImgUrl = "https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png";

        UserRoleEnum role = UserRoleEnum.USER;

        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .password(password)
                .profileImgUrl(profileImgUrl)
                .profileImgName(null)
                .role(role)
                .build();
        User saveUser = userRepository.save(user);
        Optional<User> findUser = userRepository.findById(saveUser.getUserId());
        HashMap<String, Boolean> userCheck = new HashMap<>();
        userCheck.put("signup", findUser.isPresent());
        return userCheck;
    }

    // username 중복검사
    public HashMap<String, Boolean> usernameCheck(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        Optional<User> sameUser = userRepository.findByUsername(username);
        HashMap<String, Boolean> usernameCheck = new HashMap<>();
        usernameCheck.put("isTrue", sameUser.isPresent());
        return usernameCheck;
    }

    // nickname 중복검사
    public HashMap<String, Boolean> nicknameCheck(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        Optional<User> sameNickname = userRepository.findByNickname(nickname);
        HashMap<String, Boolean> nicknameCheck = new HashMap<>();
        nicknameCheck.put("isTrue", sameNickname.isPresent());
        return nicknameCheck;
    }

    // 로그인체크
    public LoginCheckResponseDto loginChecked(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        LoginCheckResponseDto loginCheck = LoginCheckResponseDto.builder()
                        .userId(findUser.getUserId())
                        .nickname(findUser.getNickname())
                        .build();
        System.out.println(loginCheck);
        return loginCheck;
    }

    // 회원 탈퇴
    public void resignUser(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (findUser != null) {
            String username = "resignUser_"+findUser.getUserId();
            String password = passwordEncoder.encode(UUID.randomUUID().toString());
            findUser = User.builder()
                    .username(username)
                    .password(password)
                    .nickname(null)
                    .profileImgUrl(null)
                    .profileImgName(null)
                    .role(null)
                    .provider(null)
                    .providerId(null)
                    .build();
            userRepository.save(findUser);
        }
    }

    // 프로필이미지 수정
    public MypageResponseDto editProfile(MultipartFile multipartFile, UserDetailsImpl userDetails) throws IOException {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        // 기본 이미지일때는 profileImgName이 null이므로 null이 아닐때 s3에서 해당 이미지를 삭제한다.
        if (findUser.getProfileImgName() != null){
            s3UploadService.deleteFile(findUser.getProfileImgName());
        }

        ImageInfoDto imageInfoDto = s3UploadService.uploadFile(multipartFile, FolderName.PROFILE.name());

        findUser.setProfileImgName(imageInfoDto.getName());
        findUser.setProfileImgUrl(imageInfoDto.getUrl());
        User saveUser = userRepository.save(findUser);
        MypageResponseDto responseDto = MypageResponseDto.builder()
                .nickname(saveUser.getNickname())
                .userPicture(saveUser.getProfileImgUrl())
                .build();
        return responseDto;
    }

    // 닉네임 수정
    public MypageResponseDto editNickname(SignupRequestDto signupRequestDto, UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        if (findUser.getNickname().equals(signupRequestDto.getNickname())){
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }
        findUser.setNickname(signupRequestDto.getNickname());
        User saveUser = userRepository.save(findUser);
        MypageResponseDto responseDto = MypageResponseDto.builder()
                .nickname(saveUser.getNickname())
                .userPicture(saveUser.getProfileImgUrl())
                .build();
        return responseDto;
    }

}
