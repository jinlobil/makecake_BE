package com.project.makecake.service;

import com.project.makecake.model.Cake;
import com.project.makecake.model.CakeLike;
import com.project.makecake.model.User;
import com.project.makecake.repository.CakeLikeRepository;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.responseDto.CakeLikeResponseDto;
import com.project.makecake.responseDto.CakeResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CakeService {

    private final UserRepository userRepository;
    private final CakeRepository cakeRepository;
    private final CakeLikeRepository cakeLikeRepository;

    // 케이크 사진 리스트
    public List<CakeResponseDto> getAllCakes(UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        // 페이지네이션? 무한 스크롤?
        List<Cake> foundCakeList = cakeRepository.findAll();

        boolean myLike = false;

        List<CakeResponseDto> responseDtoList = new ArrayList<>();

        for (Cake cake : foundCakeList) {
            Optional<CakeLike> foundCakeLike = cakeLikeRepository.findByUserIdAndCakeId(user.getUserId(),cake.getCakeId());
            if (foundCakeLike.isPresent()) {
                myLike = true;
            }
            CakeResponseDto responseDto = new CakeResponseDto(cake,myLike);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 케이크 좋아요
    public CakeLikeResponseDto cakeLike(Long cakeId, boolean myLike,UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new IllegalArgumentException("케이크가 존재하지 않습니다."));

        // myLike가 true이면 새로운 cakeLike 저장
        if (myLike) {
            CakeLike cakeLike = new CakeLike(foundCake, user);
            cakeLikeRepository.save(cakeLike);
        // myLike가 false이면 기존 cakeLike 삭제
        } else {
            cakeLikeRepository.deleteByUserIdAndCakeId(user.getUserId(),foundCake.getCakeId());
        }
        // likeCnt 변경
        boolean likeResult = foundCake.likeCake(myLike);
        return new CakeLikeResponseDto(likeResult);

    }

}
