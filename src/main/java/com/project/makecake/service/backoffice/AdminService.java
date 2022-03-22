package com.project.makecake.service.backoffice;

import com.project.makecake.dto.UserInfoDto;
import com.project.makecake.model.Cake;
import com.project.makecake.model.User;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CakeRepository cakeRepository;

    public void editRole(Long userId, UserInfoDto dto) {
        Optional<User> findUser = userRepository.findById(userId);

    }

    // 전체 유저 불러오기
    public List<UserInfoDto> getUsers() {

        List<UserInfoDto> userList = new ArrayList<>();

        List<User> findUsers = userRepository.findAll();
        for (User user : findUsers){
            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .userId(user.getUserId())
                    .username(user.getNickname())
                    .nickname(user.getNickname())
                    .profileImgUrl(user.getProfileImgUrl())
                    .profileImgName(user.getProfileImgName())
                    .provider(user.getProvider())
                    .role(user.getRole())
                    .createdDate(user.getCreatedAt())
                    .modifiedDate(user.getModifiedAt())
                    .build();
            userList.add(userInfoDto);
        }
        return userList;
    }

    // (임시) 케이크 사진 삭제
    public void deleteCake(Long cakeId) {
        Optional<Cake> findCake = cakeRepository.findById(cakeId);
        cakeRepository.deleteById(findCake.get().getCakeId());
    }
}
