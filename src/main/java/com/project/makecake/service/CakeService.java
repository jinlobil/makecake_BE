package com.project.makecake.service;

import com.project.makecake.dto.CakeResponseDto;
import com.project.makecake.dto.HomeCakeDto;
import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.dto.LikeDto;
import com.project.makecake.enums.FolderName;
import com.project.makecake.model.Cake;
import com.project.makecake.model.CakeLike;
import com.project.makecake.model.Store;
import com.project.makecake.model.User;
import com.project.makecake.repository.CakeLikeRepository;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.repository.StoreRepository;
import com.project.makecake.repository.UserRepository;
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
    public List<HomeCakeDto> getCakeListAtHome() {
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

    // 케이크 사진 리스트 조회 메소드
    public List<CakeResponseDto> getCakeList(UserDetailsImpl userDetails, int page) {

        // 비로그인 유저는 null 처리
        User user = null;
        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        // 18개씩 페이징
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC,"likeCnt"), new Sort.Order(Sort.Direction.DESC,"cakeId"));
        Pageable pageable = PageRequest.of(page,18,sort);
        Page<Cake> foundCakeList = cakeRepository.findAll(pageable);


        // 좋아요 반영해서 반환 DTO에 담기
        List<CakeResponseDto> responseDtoList = new ArrayList<>();
        for (Cake cake : foundCakeList) {

            // myLike 디폴트는 false
            boolean myLike = false;

            // 로그인 유저는 좋아요 여부 반영
            if(user!=null) {
                Optional<CakeLike> foundCakeLike = cakeLikeRepository.findByUserAndCake(user, cake);
                if (foundCakeLike.isPresent()) {
                    myLike = true;
                }
            }

            CakeResponseDto responseDto = CakeResponseDto.builder()
                    .cake(cake)
                    .myLike(myLike)
                    .build();
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
                .orElseThrow(()->new IllegalArgumentException("케이크를 찾을 수 없습니다."));

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
                .build();
    }

    // 케이크 좋아요 생성 및 삭제 메소드
    @Transactional
    public LikeDto saveCakeLike(long cakeId, LikeDto requestDto, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        // 케이크 찾기
        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new IllegalArgumentException("케이크가 존재하지 않습니다."));

        // myLike가 true이면 새로운 cakeLike 저장
        if (requestDto.isMyLike()) {
            CakeLike cakeLike = CakeLike.builder()
                    .cake(foundCake)
                    .user(user)
                    .build();
            cakeLikeRepository.save(cakeLike);

        // myLike가 false이면 기존 cakeLike 삭제
        } else {
            cakeLikeRepository.deleteByUserAndCake(user, foundCake);
        }

        // likeCnt 변경
        boolean likeResult = foundCake.editLikeCnt(requestDto.isMyLike());
        return new LikeDto(likeResult);

    }

    // (관리자용) 가게별 케이크 사진 리스트 조회 메소드
    public List<Cake> GetCakeListAtBackoffice(long storeId) {

        Store foundStore = storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 가게입니다."));

        List<Cake> foundCakeList = cakeRepository.findAllByStore(foundStore);
        return foundCakeList;
    }

    // (관리자용) 케이크 사진 삭제 메소드
    @Transactional
    public long deleteCake(long cakeId) {

        Cake foundCake = cakeRepository.findById(cakeId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 케이크입니다."));

        // 좋아요 삭제
        cakeLikeRepository.deleteAllByCake(foundCake);

        // 케이크 삭제
        cakeRepository.delete(foundCake);

        return foundCake.getCakeId();
    }

    // (관리자용) 케이크 사진 저장 메소드
    @Transactional
    public void addCakeList(long storeId, List<MultipartFile> imgFileList) throws IOException {

        Store foundStore = storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 가게입니다."));

        if(imgFileList != null){
            for(MultipartFile imgFile : imgFileList){
                ImageInfoDto imgInfo = s3UploadService.uploadFile(imgFile, FolderName.Cake.name());
                Cake cake = Cake.builder()
                        .url(imgInfo.getUrl())
                        .store(foundStore)
                        .build();
                cakeRepository.save(cake);
            }
        }
    }
}