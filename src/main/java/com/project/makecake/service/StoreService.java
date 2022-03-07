package com.project.makecake.service;

import com.project.makecake.dto.HomeStoreDto;
import com.project.makecake.model.Store;
import com.project.makecake.repository.StoreLikeRepository;
import com.project.makecake.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreLikeRepository storeLikeRepository;

    //확장성 있게 코드 짜기 : 추후에 홈탭에서 인기순이 다른 기준으로 정렬해서 매장을 보여주게 될 수도 있으므로 getHot~ X getHomeStoreList로!
    @Transactional
    public List<HomeStoreDto> getHomeStoreList() {
        List<HomeStoreDto> homeStoreDtoList = new ArrayList<>();

        //findTop+sortNum+ByOrderBy+sortType (이런식으로 변수 받아서 바로 JPA Query 할 수 있는 법 공부해보기)
        List<Store> rawList = storeRepository.findTop5ByOrderByLikeCntDesc();

        for(Store eachStore : rawList){
            Long storeId = eachStore.getStoreId();
            String name = eachStore.getName();
            String mainImg = eachStore.getMainImg();
            int likeCnt = eachStore.getLikeCnt();

            HomeStoreDto homeStoreDto = new HomeStoreDto(storeId, name, mainImg,likeCnt);
            homeStoreDtoList.add(homeStoreDto);
        }
        return homeStoreDtoList;
    }

    @Transactional
    public void likeStore(Boolean myLike, Long storeId/*, user*/) {
        //true면 추가(좋아요 누르기), false면 삭제(좋아요 취소)
        Store store = storeRepository.findById(storeId).get();
        if (myLike) {
            store.setLikeCnt(store.getLikeCnt() +1);
            storeRepository.save(store);

            //storeLike에 추가하기 (user 구현 완료 시 넣기)
           //storeLikeRepository.save(store, user)

        }
        else{
            store.setLikeCnt(store.getLikeCnt() -1);
            storeRepository.save(store);

            //storeLikeRepository.delete~~ (storeLikeRepository.storeLikeRepository.findByStoreAndUser(store, user))
        }

    }
}
