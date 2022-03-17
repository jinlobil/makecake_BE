package com.project.makecake.controller;

import com.project.makecake.dto.*;
import com.project.makecake.model.User;
import com.project.makecake.requestDto.LikeDto;
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


    //홈탭(1) 핫매장, 핫케이크 5개씩
    @GetMapping("/api/home")
    public HomeResponseDto getHomeStoreAndCake(){
        List<HomeStoreDto> homeStoreDtoList = storeService.getHomeStoreList();
        List<HomeCakeDto> homeCakeDtoList = cakeService.getHomeCakeList();
        HomeResponseDto homeResponseDto = new HomeResponseDto(homeStoreDtoList, homeCakeDtoList);

        return homeResponseDto;
    }

    //홉탭(2) 최신 리뷰 (페이지네이션)
    @GetMapping("/api/home/review")
    public List<HomeReviewDto> getHomeReview(){
        return reviewService.getHomeReviewList();
    }

    //매장 검색 결과 보여주기

    @PostMapping("/api/search")
    public List<SearchResponseDto> getSearchStore(@RequestBody SearchRequestDto requestDto) throws IOException {
        return storeService.getSearchStore(requestDto);
    }



    //매장 상세페이지
    @GetMapping("/api/stores/{storeId}")
    public StoreDetailResponseDto getStoreDetail(@PathVariable Long storeId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return storeService.getStoreDetail(storeId, userDetails);
    }

    //매장 상세페이지 - 매장 케이크 (무한 스크롤 구현 필요) 9개씩
    @GetMapping("/api/stores/cakes")
    public List<StoreDetailCakeResponseDto> getStoreDetailCakes(@RequestParam long storeId, @RequestParam int page, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return  storeService.getStoreDetailCakes(storeId, userDetails, page);
    }


    //매장 상세페이지 - 매장 리뷰 (무한 스크롤 구현 필요) 3개씩
    @GetMapping("/api/stores/reviews")
    public List<ReviewResponseDto> getStoreDetailReviews(@RequestParam long storeId, @RequestParam int page){
        return  storeService.getStoreDetailReviews(storeId, page);
    }

    @DeleteMapping("/backOffice/stores/{storeId}")
    public void deleteStore(@PathVariable Long storeId){
        storeService.deleteStore(storeId);
    }


    //매장 좋아요
    @PostMapping("/stores/like/{storeId}")
    public LikeDto likeStore(@RequestBody LikeDto likeDto, @PathVariable Long storeId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return storeService.likeStore(likeDto.isMyLike(), storeId, user);
    }
}