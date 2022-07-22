package com.project.makecake.service;

import com.project.makecake.dto.cake.CakeListResponseDto;
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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class CakeService {

    private final CakeRepository cakeRepository;
    private final CakeLikeRepository cakeLikeRepository;

    //홈탭 케이크 불러오기 메소드
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
    public CakeListResponseDto getCakeList(
            String sortType,
            int size,
            long standard,
            int subStandard
    ) {

        return sortType.equals("random") ?
                getCakeListByRandom(size, standard) :
                getCakeListBySortType(sortType, size, standard, subStandard);

    }

    // 정렬타입에 따른 케이크 사진 리스트 조회 메소드
    public CakeListResponseDto getCakeListBySortType(String sortType, int size, long cakeId, int likeCnt) {

        List<Cake> foundCakeList = cakeId==0 ?
                cakeRepository.findOrderByLikeCnt(size+1) :
                cakeRepository.findOrderByLikeCntAndCursor(size+1, cakeId, likeCnt);

        List<CakeSimpleResponseDto> responseDtoList = new ArrayList<>();
        int responseSize = min(size, foundCakeList.size());
        for (int i=0; i<responseSize; i++) {
            Cake foundCake = foundCakeList.get(i);
            CakeSimpleResponseDto responseDto = new CakeSimpleResponseDto(foundCake);
            responseDtoList.add(responseDto);
        }

        return CakeListResponseDto.builder()
                .dtoList(responseDtoList)
                .hasNext(foundCakeList.size()==(size+1))
                .build();
    }

    // 케이크 사진 리스트 랜덤 조회 메소드
    public CakeListResponseDto getCakeListByRandom(int size, long page) {

        List<Cake> foundCakeList = cakeRepository.findByRandom(size);

        List<CakeSimpleResponseDto> responseDtoList = new ArrayList<>();
        for (Cake cake : foundCakeList) {
            CakeSimpleResponseDto responseDto = new CakeSimpleResponseDto(cake);
            responseDtoList.add(responseDto);
        }

        return CakeListResponseDto.builder()
                .dtoList(responseDtoList)
                .hasNext(page<60)
                .build();

        }


    // 케이크 사진 상세 조회 메소드
    public CakeResponseDto getCakeDetails(UserDetailsImpl userDetails, long cakeId) {

        User user = null;
        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new CustomException(ErrorCode.CAKE_NOT_FOUND));

        boolean myLike = false;

        // 좋아요 여부 반영
        if(user!=null) {
            boolean foundCakeLike = cakeLikeRepository.existsByUserAndCake(user, foundCake);
            if (foundCakeLike) {
                myLike = true;
            }
        }

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

        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new CustomException(ErrorCode.CAKE_NOT_FOUND));

        boolean existsCakeLike = cakeLikeRepository.existsByUserAndCake(user,foundCake);

        // 좋아요를 누른 경우
        if (requestDto.isMyLike()) {

            if (existsCakeLike) {
                throw new CustomException(ErrorCode.LIKE_ALREADY_EXIST);
            }

            CakeLike cakeLike = CakeLike.builder()
                    .cake(foundCake)
                    .user(user)
                    .build();
            cakeLikeRepository.save(cakeLike);

        // 좋아요를 취소한 경우
        } else {
            if (!existsCakeLike) {
                throw new CustomException(ErrorCode.LIKE_NOT_EXIST);
            }
            cakeLikeRepository.deleteByUserAndCake(user, foundCake);
        }

        foundCake.editLikeCnt(requestDto.isMyLike());

        return LikeResponseDto.builder()
                .myLike(requestDto.isMyLike())
                .likeCnt(foundCake.getLikeCnt())
                .build();
    }

}