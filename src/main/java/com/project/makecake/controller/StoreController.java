package com.project.makecake.controller;

import com.project.makecake.dto.*;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.CakeService;
import com.project.makecake.service.ReviewService;
import com.project.makecake.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StoreController {
    private final StoreService storeService;
    private final CakeService cakeService;
    private final ReviewService reviewService;


    // (홈탭) 인기 매장, 인기 케이크 5개 조회 API
    @GetMapping("/api/home")
    public HomeResponseDto getStoreAndCakeAtHome() {
        List<HomeStoreDto> storeResponseDtoList = storeService.getStoreListAtHome();
        List<HomeCakeDto> cakeResponseDtoList = cakeService.getCakeListAtHome();
        HomeResponseDto responseDto = new HomeResponseDto(storeResponseDtoList, cakeResponseDtoList);

        return responseDto;
    }

    // (홈탭) 최신 리뷰 조회 API
    @GetMapping("/api/home/review")
    public List<HomeReviewDto> getReviewListAtHome(){
        return reviewService.getReviewListAtHome();
    }


    // 매장 검색 결과 반환 API
    @PostMapping("/api/search")
    public List<SearchResponseDto> getStoreList(@RequestBody SearchRequestDto requestDto) throws IOException {
        return storeService.getStoreList(requestDto);
    }


    // 매장 상세페이지 조회 API
    @GetMapping("/api/stores/{storeId}")
    public StoreDetailResponseDto getStoreDetails(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return storeService.getStoreDetails(storeId, userDetails);
    }

    // (매장 상세페이지) 매장 케이크 조회 API (9개씩)
    @GetMapping("/api/stores/cakes")
    public List<StoreDetailCakeResponseDto> getCakeListAtStore(
            @RequestParam long storeId,
            @RequestParam int page,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return  storeService.getCakeListAtStore(storeId, userDetails, page);
    }


    // (매장 상세페이지) 매장 리뷰 조회 API (3개씩)
    @GetMapping("/api/stores/reviews")
    public List<ReviewResponseDto> getReviewListAtStore(
            @RequestParam long storeId,
            @RequestParam int page
    ) {
        return  storeService.getReviewListAtStore(storeId, page);
    }

    @DeleteMapping("/backOffice/stores/{storeId}")
    public void deleteStore(@PathVariable Long storeId){
        storeService.deleteStore(storeId);
    }


    // 매장 좋아요 API
    @PostMapping("/stores/like/{storeId}")
    public LikeDto likeStore(
            @RequestBody LikeDto requestDto,
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return storeService.likeStore(requestDto.isMyLike(), storeId, userDetails);
    }
}