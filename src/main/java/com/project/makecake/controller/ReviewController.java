package com.project.makecake.controller;

import com.project.makecake.dto.ReviewResponseDto;
import com.project.makecake.dto.ReviewResponseTempDto;
import com.project.makecake.model.Review;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //매장 후기 쓰기
    @PostMapping("/reviews/{storeId}")
    public void writeReview(@PathVariable long storeId,
                            @RequestPart(value="content") String content,
                            @RequestParam(required = false) List<MultipartFile> imgFiles,
                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        reviewService.writeReview(storeId, content, imgFiles, userDetails);
    }

    //매장 후기 상세 조회
    @GetMapping("/reviews/{reviewId}")
    public ReviewResponseTempDto getReviewDetail(@PathVariable long reviewId){
        return reviewService.getReviewDetail(reviewId);
    }

    //매장 후기 수정
    @PutMapping("/reviews/{reviewId}")
    public void updateReview(@PathVariable long reviewId,
                             @RequestParam(value="content") String content,
                             @RequestPart(required = false) List<MultipartFile> imgFiles,
                             @RequestPart(value="imgUrls") List<String> imgUrls,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException{
        reviewService.updateReview(reviewId, content, imgFiles, imgUrls, userDetails);
    }


    //매장 후기 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public void deleteReview(@PathVariable long reviewId){
        reviewService.deleteReview(reviewId);
    }
}
