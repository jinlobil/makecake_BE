package com.project.makecake.controller;

import com.project.makecake.dto.*;
import com.project.makecake.responseDto.DesignResponseDto;
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

    // 마이페이지 조회
    @GetMapping("/mypage")
    public MypageResponseDto getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.mypage(userDetails);
    }

    // 내가 그린 도안 조회
    @GetMapping("/designs/mine")
    public List<MyDesignResponseDto> myDesigns(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestParam String option,
                                               @RequestParam int page) {
        return mypageService.myDesigns(userDetails, option, page);
    }

    // 내가 그린 도안 상세 조회(게시X)
    @GetMapping("/designs/mine/{designId}")
    public DesignResponseDto getDesign(@PathVariable Long designId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getDesign(designId, userDetails);
    }

    // 내가 좋아요한 게시글
    @GetMapping("/designs/myReact")
    public List<MyReactPostResponceDto> myReactDesigns(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam int page) {
        return mypageService.myReactDesigns(userDetails, page);
    }

    // 내가 남긴 댓글
    @GetMapping("/designs/myComment")
    public List<MyCommentResponseDto> myComments(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam int page) {
        return mypageService.myComments(userDetails, page);
    }

    // 내가 좋아요한 매장
    @GetMapping("/stores/myReact")
    public List<MyReactStoreResponseDto> myReactStores(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam int page) {
        return mypageService.myReactStores(userDetails, page);
    }

    // 내가 남긴 후기
    @GetMapping("/stores/myReview")
    public List<MyReviewResponseDto> myReviews(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam int page) {
        return mypageService.myReviews(userDetails, page);
    }

    // 내가 좋아요한 케이크
    @GetMapping("/cakes/myReact")
    public List<MyReactCakeResponseDto> myReactCakes(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam int page) {
        return mypageService.myReactCakes(userDetails, page);
    }
}
