package com.project.makecake.controller;

import com.project.makecake.dto.*;
import com.project.makecake.dto.DesignResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    // 나의 프로필 조회 API
    @GetMapping("/mypage")
    public MypageResponseDto getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getMyProfile(userDetails);
    }

    // 내가 그린 도안 조회 API
    @GetMapping("/designs/mine")
    public List<MyDesignResponseDto> getMyDesignList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String option,
            @RequestParam int page
    ) {
        return mypageService.getMyDesignList(userDetails, option, page);
    }

    // 내가 게시 안 한 도안 상세 조회 API
    @GetMapping("/designs/mine/{designId}")
    public DesignResponseDto getDesignDetails(
            @PathVariable Long designId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return mypageService.getDesignDetails(designId, userDetails);
    }

    // 내가 좋아요한 게시글 API
    @GetMapping("/designs/myReact")
    public List<MyReactPostResponceDto> getMyLikePostList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page
    ) {
        return mypageService.getMyLikePostList(userDetails, page);
    }

    // 내가 남긴 댓글 API
    @GetMapping("/designs/myComment")
    public List<MyCommentResponseDto> getMyCommentList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page
    ) {
        return mypageService.getMyCommentList(userDetails, page);
    }

    // 내가 좋아요한 매장 API
    @GetMapping("/stores/myReact")
    public List<MyReactStoreResponseDto> getMyLikeStoreList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page
    ) {
        return mypageService.getMyLikeStoreList(userDetails, page);
    }

    // 내가 남긴 후기 API
    @GetMapping("/stores/myReview")
    public List<MyReviewResponseDto> getMyReviewList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page
    ) {
        return mypageService.getMyReviewList(userDetails, page);
    }

    // 내가 좋아요한 케이크 API
    @GetMapping("/cakes/myReact")
    public List<MyReactCakeResponseDto> getMyLikeCakeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page
    ) {
        return mypageService.getMyLikeCakeList(userDetails, page);
    }
}
