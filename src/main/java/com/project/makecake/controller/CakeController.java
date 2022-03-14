package com.project.makecake.controller;

import com.project.makecake.model.Cake;
import com.project.makecake.requestDto.CakeIdRequestDto;
import com.project.makecake.requestDto.LikeRequestDto;
import com.project.makecake.requestDto.TempCakeRequestDto;
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

    // 18개씩
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
    public CakeResponseDto getCake(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CakeIdRequestDto requestDto
            ) {
        return cakeService.getCake(userDetails,requestDto.getCakeId());
    }

    // 케이크 좋아요 누르기 API
    @PostMapping("/cakes/like/{cakeId}")
    public LikeResponseDto cakeLike(@PathVariable Long cakeId,
                                    @RequestBody LikeRequestDto likeRequestDto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails
                                        ) {
        return cakeService.cakeLike(cakeId,likeRequestDto,userDetails);
    }


    // 임시 API (가게별 케이크 사진 불러오기)
    @GetMapping("/api/temp/cakes/{storeId}")
    public List<Cake> tempGetCake(@PathVariable Long storeId) {
        return cakeService.tempGetCake(storeId);
    }

    // 임시 API (케이크 사진 지우기)
    @DeleteMapping("/api/temp/cakes/{cakeId}")
    public Long tempDeleteCake(@PathVariable Long cakeId) {
        return cakeService.tempDeleteCake(cakeId);
    }

    // 임시 API (케이크 사진 넣기)
    @PostMapping("/api/temp/cakes/{storeId}")
    public void tempSaveCake(@PathVariable Long storeId, @RequestBody TempCakeRequestDto requestDto) {
        cakeService.tempSaveCake(storeId,requestDto);
    }

}
