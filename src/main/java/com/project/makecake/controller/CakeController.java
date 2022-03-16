package com.project.makecake.controller;

import com.project.makecake.dto.TempCakeModalDto;
import com.project.makecake.model.Cake;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.requestDto.CakeIdRequestDto;
import com.project.makecake.requestDto.LikeRequestDto;
import com.project.makecake.responseDto.LikeResponseDto;
import com.project.makecake.responseDto.CakeResponseDto;
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
    private final CakeRepository cakeRepository;

    // 일단 15개
    // 케이크 사진 리스트 API
    @GetMapping("/api/cakes")
    public List<CakeResponseDto> getAllCakes(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page
            ) {
        return cakeService.getAllCakes(userDetails,page);
    }

    // 케이크 사진 모달 API
    @PostMapping("/api/cakes/detail")
    public TempCakeModalDto getCake(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CakeIdRequestDto requestDto
            ) {
        Long storeId = cakeRepository.findById(requestDto.getCakeId()).get().getStore().getStoreId();
        TempCakeModalDto tempCakeModalDto = new TempCakeModalDto(cakeService.getCake(userDetails,requestDto.getCakeId()), storeId);
        return tempCakeModalDto;
    }

    // 케이크 좋아요 누르기 API
    @PostMapping("/cakes/like/{cakeId}")
    public LikeResponseDto cakeLike(@PathVariable Long cakeId,
                                    @RequestBody LikeRequestDto likeRequestDto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails
                                        ) {
        return cakeService.cakeLike(cakeId,likeRequestDto,userDetails);
    }

}
