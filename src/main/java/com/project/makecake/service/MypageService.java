package com.project.makecake.service;

import com.project.makecake.dto.MypageResponseDto;
import com.project.makecake.model.User;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final UserRepository userRepository;

    public MypageResponseDto mypage(UserDetailsImpl userDetails) {
        Optional<User> findUser = userRepository.findByUsername(userDetails.getUsername());
        MypageResponseDto mypage = new MypageResponseDto();
        mypage.setNickname(findUser.get().getNickname());

        return mypage;
    }


}
