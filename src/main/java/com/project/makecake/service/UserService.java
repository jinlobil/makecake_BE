package com.project.makecake.service;

import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.dto.LoginCheckResponseDto;
import com.project.makecake.dto.MypageResponseDto;
import com.project.makecake.dto.SignupRequestDto;
import com.project.makecake.enums.FolderName;
import com.project.makecake.enums.UserRoleEnum;
import com.project.makecake.model.User;
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
    public HashMap<String, Boolean> addUser(SignupRequestDto requestDto) {
        // 유효성 검사
        validate(requestDto);

        String username = requestDto.getUsername();

        String nickname = requestDto.getNickname();

        // 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 기본 프로필 이미지
        String profileImgUrl = "https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png";

        // 권한 : USER
        UserRoleEnum role = UserRoleEnum.USER;

        // 유저 회원가입
        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .password(password)
                .profileImgUrl(profileImgUrl)
                .profileImgName(null)
                .role(role)
                .build();
        User saveUser = userRepository.save(user);

        // 회원가입 확인
        Optional<User> foundUser = userRepository.findById(saveUser.getUserId());
        HashMap<String, Boolean> responseDto = new HashMap<>();
        responseDto.put("signup", foundUser.isPresent());
        return responseDto;
    }

    // 유효성 검사
    private void validate(SignupRequestDto requestDto) {
        // username 중복체크
        Optional<User> checkUsername = userRepository.findByUsername(requestDto.getUsername());
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 아이디가 존재합니다.");
        }

        // nickname 중복체크
        Optional<User> checkNickname = userRepository.findByNickname(requestDto.getNickname());
        if (checkNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }

        // 패스워드 일치 확인
        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
    }

    // username 중복검사
    public HashMap<String, Boolean> checkUsername(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        Optional<User> sameUser = userRepository.findByUsername(username);
        HashMap<String, Boolean> responseDto = new HashMap<>();
        responseDto.put("isTrue", sameUser.isPresent());
        return responseDto;
    }

    // nickname 중복검사
    public HashMap<String, Boolean> checkNickname(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        Optional<User> sameNickname = userRepository.findByNickname(nickname);
        HashMap<String, Boolean> responseDto = new HashMap<>();
        responseDto.put("isTrue", sameNickname.isPresent());
        return responseDto;
    }

    // 로그인체크
    public LoginCheckResponseDto checkLogin(UserDetailsImpl userDetails) {
        User foundUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        LoginCheckResponseDto responseDto = LoginCheckResponseDto.builder()
                        .userId(foundUser.getUserId())
                        .nickname(foundUser.getNickname())
                        .build();
        System.out.println(responseDto);
        return responseDto;
    }

    // 회원 탈퇴 (회원삭제가 아니라 회원정보를 삭제)
    public MypageResponseDto resignUser(UserDetailsImpl userDetails) {
        User foundUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (foundUser != null) {
            String username = "resignUser_"+foundUser.getUserId();
            String password = passwordEncoder.encode(UUID.randomUUID().toString());
            String nickname = "알수없음";
            foundUser.setUsername(username);
            foundUser.setNickname(nickname);
            foundUser.setPassword(password);
            foundUser.setProfileImgUrl("https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png");
            foundUser.setProfileImgName(null);
            foundUser.setRole(null);
            foundUser.setProvider(null);
            foundUser.setProviderEmail(null);
            foundUser.setProviderId(null);
            userRepository.save(foundUser);
        }
        String email = foundUser.getUsername();
        if (foundUser.getProviderEmail() != null){
            email = foundUser.getProviderEmail();
        }
        MypageResponseDto responseDto = MypageResponseDto.builder()
                .nickname(foundUser.getNickname())
                .profileImg(foundUser.getProfileImgUrl())
                .email(email)
                .build();
        return responseDto;
    }

    // 프로필이미지 수정
    public MypageResponseDto editProfile(MultipartFile imgFile, UserDetailsImpl userDetails) throws IOException {
        User foundUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        // 기본 이미지일때는 profileImgName이 null이므로 null이 아닐때 s3에서 해당 이미지를 삭제한다.
        if (foundUser.getProfileImgName() != null){
            s3UploadService.deleteFile(foundUser.getProfileImgName());
        }

        ImageInfoDto imageInfoDto = s3UploadService.uploadFile(imgFile, FolderName.PROFILE.name());

        foundUser.setProfileImgName(imageInfoDto.getName());
        foundUser.setProfileImgUrl(imageInfoDto.getUrl());
        User saveUser = userRepository.save(foundUser);
        MypageResponseDto responseDto = MypageResponseDto.builder()
                .nickname(saveUser.getNickname())
                .profileImg(saveUser.getProfileImgUrl())
                .build();
        return responseDto;
    }

    // 닉네임 수정
    public MypageResponseDto editNickname(SignupRequestDto signupRequestDto, UserDetailsImpl userDetails) {
        User foundUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        if (foundUser.getNickname().equals(signupRequestDto.getNickname())){
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }
        foundUser.setNickname(signupRequestDto.getNickname());
        User saveUser = userRepository.save(foundUser);
        MypageResponseDto responseDto = MypageResponseDto.builder()
                .nickname(saveUser.getNickname())
                .profileImg(saveUser.getProfileImgUrl())
                .build();
        return responseDto;
    }
}
