package com.project.makecake.controller;

import com.project.makecake.dto.*;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    // 마이페이지 조회
    @GetMapping("/mypage")
    public MypageResponseDto getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.mypage(userDetails);
    }

    // 내가 그린 도안 조회
    @GetMapping("/designs/mine/{option}")
    public List<MyDesignResponseDto> myDesigns(@PathVariable String option, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.myDesigns(option, userDetails);
    }

    // 내가 좋아요한 게시글
    @GetMapping("/designs/myReact")
    public List<MyReactDesignResponceDto> myReactDesigns(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.myReactDesigns(userDetails);
    }

    // 내가 남긴 댓글
    @GetMapping("/designs/myComment")
    public List<MyCommentResponseDto> myComments(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.myComments(userDetails);
    }

    // 내가 좋아요한 매장
    @GetMapping("/stores/myReact")
    public List<MyReactStoreResponseDto> myReactStores(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.myReactStores(userDetails);
    }

    // 내가 남긴 후기
    @GetMapping("/stores/myReview")
    public List<MyReviewResponseDto> myReviews(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.myReviews(userDetails);
    }

    // 내가 좋아요한 케이크
    @GetMapping("/cakes/myReact")
    public List<MyReactCakeResponseDto> myReactCakes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.myReactCakes(userDetails);
    }
}
