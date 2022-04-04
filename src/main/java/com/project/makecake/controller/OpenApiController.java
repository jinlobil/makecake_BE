package com.project.makecake.controller;

import com.project.makecake.dto.openapi.OpenApiImgDto;
import com.project.makecake.model.Cake;
import com.project.makecake.model.Store;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class OpenApiController {
    private final StoreRepository storeRepository;
    private final CakeRepository cakeRepository;

    //케이크 사진 가져오기 메소드
    @GetMapping("/cake")
    public List<OpenApiImgDto> getCakes(){

        List<OpenApiImgDto> imageListResponse = new ArrayList<>();
        List<Store> storeList = storeRepository.findAll();
        for(int i = 0; i < storeList.size(); i++) {
            OpenApiImgDto openApiImgDto = new OpenApiImgDto();
            String storeName = storeList.get(i).getName();
            List<String> imageList = new ArrayList<>();
            List<Cake> cakeList = cakeRepository.findAllByStore(storeList.get(i));
            for(int j = 0; j < cakeList.size(); j++){
                String imageUrl = cakeList.get(j).getUrl();
                imageList.add(imageUrl);
            }
            openApiImgDto.setStoreName(storeName);
            openApiImgDto.setImageList(imageList);

            imageListResponse.add(openApiImgDto);
        }

        return imageListResponse;

    }

    }
