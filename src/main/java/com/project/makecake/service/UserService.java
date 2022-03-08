package com.project.makecake.service;

import com.project.makecake.dto.LoginCheckResponseDto;
import com.project.makecake.dto.SignupRequestDto;
import com.project.makecake.model.User;
import com.project.makecake.model.UserRoleEnum;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 회원가입
    public User registerUser(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();

        String nickname = requestDto.getNickname();

        String password = passwordEncoder.encode(requestDto.getPassword());

        String userPicture = "";

        UserRoleEnum role = UserRoleEnum.USER;

        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .password(password)
                .userPicture(userPicture)
                .role(role)
                .build();
        return userRepository.save(user);

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
    public LoginCheckResponseDto loginCheck(UserDetailsImpl userDetails) {
        LoginCheckResponseDto loginCheck = new LoginCheckResponseDto();
        loginCheck.setUId(userDetails.getUser().getUserId());
        loginCheck.setNickname(userDetails.getNickname());
        return loginCheck;
    }
}
