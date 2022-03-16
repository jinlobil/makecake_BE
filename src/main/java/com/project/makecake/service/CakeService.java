package com.project.makecake.service;

import com.project.makecake.dto.HomeCakeDto;
import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.enums.FolderName;
import com.project.makecake.model.*;
import com.project.makecake.repository.CakeLikeRepository;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.repository.StoreRepository;
import com.project.makecake.repository.UserRepository;
import com.project.makecake.requestDto.LikeDto;
import com.project.makecake.responseDto.CakeResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CakeService {
    private final UserRepository userRepository;
    private final CakeRepository cakeRepository;
    private final CakeLikeRepository cakeLikeRepository;
    //임시
    private final StoreRepository storeRepository;
    private final S3UploadService s3UploadService;


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

    // 케이크 사진 리스트 메소드
    @Transactional
    public List<CakeResponseDto> getAllCakes(UserDetailsImpl userDetails,int page) {
        // 비로그인 유저는 null 처리
        User user = null;
        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        // 일단 15개씩 페이징
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC,"likeCnt"), new Sort.Order(Sort.Direction.DESC,"cakeId"));
        Pageable pageable = PageRequest.of(page,18,sort);
        Page<Cake> foundCakeList = cakeRepository.findAll(pageable);


        // 반환 Dto에 담기 + 좋아요 반영
        List<CakeResponseDto> responseDtoList = new ArrayList<>();
        for (Cake cake : foundCakeList) {
            boolean myLike = false; // myLike 디폴트 : false
            if(user!=null) { // 로그인 유저는 좋아요 여부 반영
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

    // 케이크 사진 상세
    public CakeResponseDto getCake(UserDetailsImpl userDetails, Long cakeId) {
        // 비로그인 유저는 null 처리
        User user = null;
        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        // 케이크 찾아오기
        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new IllegalArgumentException("케이크를 찾을 수 없습니다."));

        // 좋아요 반영
        boolean myLike = false; // myLike 디폴트 : false
        if(user!=null) { // 로그인 유저는 좋아요 여부 반영
            Optional<CakeLike> foundCakeLike = cakeLikeRepository.findByUserAndCake(user, foundCake);
            if (foundCakeLike.isPresent()) {
                myLike = true;
            }
        }

        // Dto에 담아 반환
        return new CakeResponseDto(foundCake,myLike);
    }

    // 케이크 좋아요
    @Transactional
    public LikeDto cakeLike(Long cakeId, LikeDto requestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        // 케이크 찾기
        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new IllegalArgumentException("케이크가 존재하지 않습니다."));

        // myLike가 true이면 새로운 cakeLike 저장
        if (requestDto.isMyLike()) {
            CakeLike cakeLike = new CakeLike(foundCake, user);
            cakeLikeRepository.save(cakeLike);
            // myLike가 false이면 기존 cakeLike 삭제
        } else {
            cakeLikeRepository.deleteByUserAndCake(user,foundCake);
        }
        // likeCnt 변경
        boolean likeResult = foundCake.likeCake(requestDto.isMyLike());
        return new LikeDto(likeResult);

    }

    // 임시 API (가게별 케이크 사진 불러오기)
    @Transactional
    public List<Cake> tempGetCake(Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("스토어 없음"));
        List<Cake> cakeList = cakeRepository.findAllByStore(store);
        return cakeList;
    }

    // 임시 API (케이크 사진 지우기)
    @Transactional
    public Long tempDeleteCake(Long cakeId) {
        Cake cake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new IllegalArgumentException("케이크 없음"));

        Long num = cake.getCakeId();

        cakeRepository.delete(cake);

        return num;
    }

    // 임시 API (케이크 사진 넣기)
    @Transactional
    public void tempSaveCake(Long storeId, List<MultipartFile> imgFiles) throws IOException {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("스토어 없음"));

        if(imgFiles != null){
            for(MultipartFile imgFile : imgFiles){
                ImageInfoDto imageInfoDto = s3UploadService.uploadFile(imgFile, FolderName.Cake.name());
                Cake cake = new Cake(imageInfoDto.getUrl(),store);
                cakeRepository.save(cake);
            }
        }
    }
}