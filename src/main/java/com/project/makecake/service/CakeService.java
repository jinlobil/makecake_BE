package com.project.makecake.service;

import com.project.makecake.dto.HomeCakeDto;
import com.project.makecake.dto.HomeStoreDto;
import com.project.makecake.model.Cake;
import com.project.makecake.model.CakeLike;
import com.project.makecake.model.User;
import com.project.makecake.repository.CakeLikeRepository;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.responseDto.CakeResponseDto;
import com.project.makecake.responseDto.LikeResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CakeService {
    private final UserRepository userRepository;
    private final CakeRepository cakeRepository;
    private final CakeLikeRepository cakeLikeRepository;

    //홈탭 케이크 불러오기
    @Transactional
    public List<HomeCakeDto> getHomeCakeList() {
        List<HomeCakeDto> homeCakeDtoList = new ArrayList<>();
        List<Cake> rawCakeList = cakeRepository.findTop5ByOrderByLikeCnt();

        for(Cake eachCake : rawCakeList){
            Long cakeId = eachCake.getCakeId();
            String mainImg = eachCake.getUrl();
            int likeCnt = eachCake.getLikeCnt();

            HomeCakeDto homeCakeDto = new HomeCakeDto(cakeId, mainImg, likeCnt);
            homeCakeDtoList.add(homeCakeDto);
        }
        return homeCakeDtoList;
    }

    // 케이크탭 케이크 불러오기
    @Transactional
    public List<CakeResponseDto> getAllCakes(UserDetailsImpl userDetails, int page, int size) {
        User user = null;

        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        // 페이지네이션? 무한 스크롤?
        Sort sort = Sort.by(Sort.Direction.DESC,"likeCnt");
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Cake> foundCakeList = cakeRepository.findAll(pageable);

        List<CakeResponseDto> responseDtoList = new ArrayList<>();

        for (Cake cake : foundCakeList) {
            boolean myLike = false;
            if(user!=null) {
                Optional<CakeLike> foundCakeLike = cakeLikeRepository.findByUserAndCake(user,cake);
                if (foundCakeLike.isPresent()) {
                    myLike = true;
                }
            }
            CakeResponseDto responseDto = new CakeResponseDto(cake,myLike);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 케이크 좋아요
    @Transactional
    public LikeResponseDto cakeLike(Long cakeId, boolean myLike, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new IllegalArgumentException("케이크가 존재하지 않습니다."));

        // myLike가 true이면 새로운 cakeLike 저장
        if (myLike) {
            CakeLike cakeLike = new CakeLike(foundCake, user);
            cakeLikeRepository.save(cakeLike);
            // myLike가 false이면 기존 cakeLike 삭제
        } else {
            cakeLikeRepository.deleteByUserAndCake(user,foundCake);
        }
        // likeCnt 변경
        boolean likeResult = foundCake.likeCake(myLike);
        return new LikeResponseDto(likeResult);

    }


}