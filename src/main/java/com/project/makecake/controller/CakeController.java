package com.project.makecake.controller;

import com.project.makecake.dto.cake.CakeListResponseDto;
import com.project.makecake.dto.cake.CakeResponseDto;
import com.project.makecake.dto.cake.CakeSimpleResponseDto;
import com.project.makecake.dto.like.LikeRequestDto;
import com.project.makecake.dto.like.LikeResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.CakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CakeController {

    private final CakeService cakeService;

    // 케이크 사진 리스트 조회 API (54개씩)
    @GetMapping("/cakes")
    public CakeListResponseDto getCakeList(
            @RequestParam String sortType,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "0") long standard,
            @RequestParam(required = false, defaultValue = "0") int subStandard
    ) {
        return cakeService.getCakeList(sortType, size, standard, subStandard);
    }

    // 케이크 사진 상세 조회 API
    @GetMapping("/cakes/{cakeId}/detail")
    public CakeResponseDto getCakeDetails(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable long cakeId
    ) {
        return cakeService.getCakeDetails(userDetails, cakeId);
    }

    // 케이크 좋아요 생성 및 삭제 API
    @PostMapping("/cakes/{cakeId}/like")
    public LikeResponseDto saveCakeLike(
            @PathVariable long cakeId,
            @RequestBody LikeRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return cakeService.saveCakeLike(cakeId, requestDto, userDetails);
    }

}
