package com.project.makecake.service;

import com.project.makecake.dto.cake.CakeResponseDto;
import com.project.makecake.dto.cake.CakeSimpleResponseDto;
import com.project.makecake.dto.home.HomeCakeDto;
import com.project.makecake.dto.like.LikeRequestDto;
import com.project.makecake.dto.like.LikeResponseDto;
import com.project.makecake.exceptionhandler.CustomException;
import com.project.makecake.exceptionhandler.ErrorCode;
import com.project.makecake.model.Cake;
import com.project.makecake.model.CakeLike;
import com.project.makecake.model.User;
import com.project.makecake.repository.CakeLikeRepository;
import com.project.makecake.repository.CakeRepository;
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
    private final CakeRepository cakeRepository;
    private final CakeLikeRepository cakeLikeRepository;

    //홈탭 케이크 불러오기
    @Transactional
    public List<HomeCakeDto> getCakeListAtHome() {
        List<HomeCakeDto> homeCakeDtoList = new ArrayList<>();
        List<Cake> rawCakeList = cakeRepository.findTop5ByOrderByLikeCntDesc();

        for(Cake eachCake : rawCakeList){
            Long cakeId = eachCake.getCakeId();
            String thumbnailMainImg = eachCake.getThumbnailUrl();
            int likeCnt = eachCake.getLikeCnt();

            HomeCakeDto homeCakeDto = HomeCakeDto.builder()
                    .cakeId(cakeId)
                    .mainImg(thumbnailMainImg)
                    .likeCnt(likeCnt)
                    .build();
            homeCakeDtoList.add(homeCakeDto);
        }
        return homeCakeDtoList;
    }

    // 케이크 사진 리스트 조회 메소드
    public List<CakeSimpleResponseDto> getCakeList(int page, String sortType) {

        if (page>=60 && sortType.equals("random")) {
            return new ArrayList<>();
        }

        List<Cake> foundCakeList = findCakeListBySortType(page, sortType);

        List<CakeSimpleResponseDto> responseDtoList = new ArrayList<>();
        for (Cake cake : foundCakeList) {
            CakeSimpleResponseDto responseDto = new CakeSimpleResponseDto(cake);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 케이크 사진 상세 조회 메소드
    public CakeResponseDto getCakeDetails(UserDetailsImpl userDetails, long cakeId) {

        // 비로그인 유저는 null 처리
        User user = null;
        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        // 케이크 찾아오기
        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new CustomException(ErrorCode.CAKE_NOT_FOUND));

        // myLike 디폴트는 false
        boolean myLike = false;

        // 로그인 유저는 좋아요 여부 반영
        if(user!=null) {
            Optional<CakeLike> foundCakeLike = cakeLikeRepository.findByUserAndCake(user, foundCake);
            if (foundCakeLike.isPresent()) {
                myLike = true;
            }
        }

        // DTO에 담아 반환
        return CakeResponseDto.builder()
                .cake(foundCake)
                .myLike(myLike)
                .imgUrl(foundCake.getUrl())
                .build();
    }

    // 케이크 좋아요 생성 및 삭제 메소드
    @Transactional
    public LikeResponseDto saveCakeLike(long cakeId, LikeRequestDto requestDto, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        // 케이크 찾기
        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new CustomException(ErrorCode.CAKE_NOT_FOUND));

        // 케이크 좋아요 찾기
        boolean existsCakeLike = cakeLikeRepository.existsByUserAndCake(user,foundCake);

        // myLike가 true이면 새로운 cakeLike 저장
        if (requestDto.isMyLike()) {

            // 이미 좋아요를 누른 케이크이면 exception
            if (existsCakeLike) {
                throw new CustomException(ErrorCode.LIKE_ALREADY_EXIST);
            }

            CakeLike cakeLike = CakeLike.builder()
                    .cake(foundCake)
                    .user(user)
                    .build();
            cakeLikeRepository.save(cakeLike);

        // myLike가 false이면 기존 cakeLike 삭제
        } else {

            // 좋아요를 누르지 않은 케이크이면 exception
            if (!existsCakeLike) {
                throw new CustomException(ErrorCode.LIKE_NOT_EXIST);
            }

            cakeLikeRepository.deleteByUserAndCake(user, foundCake);
        }

        // likeCnt 변경
        foundCake.editLikeCnt(requestDto.isMyLike());

        return LikeResponseDto.builder()
                .myLike(requestDto.isMyLike())
                .likeCnt(foundCake.getLikeCnt())
                .build();

    }


    // 케이크 리스트 찾기 메소드
    public List<Cake> findCakeListBySortType(int page, String sortType) {

        List<Cake> foundCakeList = new ArrayList<>();

        if (sortType.equals("likeCnt")) {
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC,"likeCnt"), new Sort.Order(Sort.Direction.DESC,"cakeId"));
            Pageable pageable = PageRequest.of(page,54,sort);
            Page<Cake> foundCakePage = cakeRepository.findAll(pageable);

            for (Cake cake : foundCakePage) {
                foundCakeList.add(cake);
            }
        } else {
            foundCakeList = cakeRepository.findByRandom();
        }

        return foundCakeList;
    }
}