package com.project.makecake.controller;

import com.project.makecake.dto.HomeCakeDto;
import com.project.makecake.dto.HomeResponseDto;
import com.project.makecake.dto.HomeReviewDto;
import com.project.makecake.dto.HomeStoreDto;
import com.project.makecake.model.User;
import com.project.makecake.service.CakeService;
import com.project.makecake.service.ReviewService;
import com.project.makecake.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StoreController {
    private final StoreService storeService;
    private final CakeService cakeService;
    private final ReviewService reviewService;


    @GetMapping("/api/home")
    public HomeResponseDto getHome(){
        List<HomeStoreDto> homeStoreDtoList = storeService.getHomeStoreList();
        List<HomeCakeDto> homeCakeDtoList = cakeService.getHomeCakeList();
        HomeResponseDto homeResponseDto = new HomeResponseDto(homeStoreDtoList, homeCakeDtoList);

        return homeResponseDto;
    }

    @PostMapping("/stores/like/{storeId}")
    public void likeStore(@RequestBody Boolean myLike, @PathVariable Long storeId /*@AuthenticationPrincipal UserDetailsImpl userDetails*/){
//        User user = userDetails.getUser();
        storeService.likeStore(myLike, storeId/*, user*/);
    }
}
