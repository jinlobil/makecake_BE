package com.project.makecake.controller;

import com.project.makecake.responseDto.CakeLikeResponseDto;
import com.project.makecake.responseDto.CakeResponseDto;
import com.project.makecake.service.CakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CakeController {

    private final CakeService cakeService;

    // 케이크 사진 리스트 API
    @GetMapping("/api/cakes")
    public List<CakeResponseDto> getAllCakes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cakeService.getAllCakes(userDetails);
    }

    // 케이크 좋아요 누르기 API
    @PostMapping("/cakes/like/{cakeId}")
    public CakeLikeResponseDto cakeLike(@PathVariable Long cakeId,
                                        @RequestBody boolean myLike,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails
                                        ) {
        return cakeService.cakeLike(cakeId,myLike
                //,userDetails
        );
    }

}
