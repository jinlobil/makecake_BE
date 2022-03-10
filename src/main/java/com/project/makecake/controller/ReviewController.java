package com.project.makecake.controller;

import com.project.makecake.requestDto.ReviewRequestDto;
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
                            @RequestPart(value="requestDto") ReviewRequestDto requestDto,
                            @RequestParam(required = false) List<MultipartFile> imgFiles,
                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        reviewService.writeReview(storeId, requestDto, imgFiles, userDetails);
    }

    //매장 후기 수정
    @PutMapping("/reviews/{reviewId}")
    public void updateReview(@PathVariable long reviewId,
                             @RequestPart(value="requestDto") ReviewRequestDto requestDto,
                             @RequestPart(required = false) List<MultipartFile> imgFiles,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException{
        reviewService.updateReview(reviewId, requestDto, imgFiles, userDetails);
    }


    //매장 후기 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public void deleteReview(@PathVariable long reviewId){
        reviewService.deleteReview(reviewId);
    }
}
