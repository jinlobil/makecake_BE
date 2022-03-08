package com.project.makecake.controller;

import com.project.makecake.requestDto.LikeRequestDto;
import com.project.makecake.responseDto.CakeLikeResponseDto;
import com.project.makecake.responseDto.CakeResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.CakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CakeController {

    private final CakeService cakeService;

    // 케이크 사진 리스트 API
    @GetMapping("/api/cakes")
    public List<CakeResponseDto> getAllCakes(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestParam int page,
                                             @RequestParam int size) {
        return cakeService.getAllCakes(userDetails,page,size);
    }

    // 케이크 좋아요 누르기 API
    @PostMapping("/cakes/like/{cakeId}")
    public CakeLikeResponseDto cakeLike(@PathVariable Long cakeId,
                                        @RequestBody LikeRequestDto likeRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails
                                        ) {
        return cakeService.cakeLike(cakeId,likeRequestDto.isMyLike(),userDetails);
    }

}
