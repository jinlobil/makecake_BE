package com.project.makecake.service;

import com.project.makecake.dto.HomeCakeDto;
import com.project.makecake.dto.HomeStoreDto;
import com.project.makecake.model.Cake;
import com.project.makecake.repository.CakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CakeService {
    private final CakeRepository cakeRepository;


    public List<HomeCakeDto> getHomeCakeList() {
        List<HomeCakeDto> homeCakeDtoList = new ArrayList<>();
        List<Cake> rawCakeList = cakeRepository.findTop5ByOrderByLikeCnt();

        for(Cake eachCake : rawCakeList){
            Long cakeId = eachCake.getCakeId();
            String mainImg = eachCake.getUrl();
            int likeCnt = eachCake.getLikeCnt();

            HomeCakeDto homeCakeDto = new HomeCakeDto(cakeId, mainImg, likeCnt);
            homeCakeDtoList.add(homeCakeDto);
        }
        return homeCakeDtoList;
    }
}
