package com.project.makecake.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.project.makecake.dto.*;
import com.project.makecake.exceptionhandler.CustomException;
import com.project.makecake.exceptionhandler.ErrorCode;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.backoffice.OrderFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final StoreUrlRepository storeUrlRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final CakeRepository cakeRepository;
    private final CakeLikeRepository cakeLikeRepository;
    private final OpenTimeRepository openTimeRepository;
    private final CakeMenuRepository cakeMenuRepository;
    private final OpenApiService openApiService;
    private final OrderFormService orderFormService;

    // (홈탭) 인기 매장 리스트 조회 메소드
    public List<HomeStoreDto> getStoreListAtHome() {
        List<HomeStoreDto> responseDtoList = new ArrayList<>();

        List<Store> foundStoreList = storeRepository.findTop5ByOrderByLikeCntDesc();

        for (Store eachStore : foundStoreList) {
            Long storeId = eachStore.getStoreId();
            String name = eachStore.getName();
            String mainImg = eachStore.getMainImg();
            int likeCnt = eachStore.getLikeCnt();

            HomeStoreDto responseDto = new HomeStoreDto(storeId, name, mainImg, likeCnt);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 매장 좋아요 메소드
    @Transactional
    public LikeResponseDto likeStore(Boolean myLike, Long storeId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        //true 추가(좋아요 누르기), false 삭제(좋아요 취소)
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (myLike) {
            StoreLike storeLike = new StoreLike();
            storeLike.setStore(store);
            storeLike.setUser(user);
            storeLikeRepository.save(storeLike);
            store.setLikeCnt(store.getLikeCnt() +1);
            storeRepository.save(store);
            return LikeResponseDto.builder().myLike(true).likeCnt(store.getLikeCnt()).build();
        } else {
            storeLikeRepository.deleteByStoreAndUser(store, user);
            store.setLikeCnt(store.getLikeCnt() -1);
            storeRepository.save(store);
            return LikeResponseDto.builder().myLike(false).likeCnt(store.getLikeCnt()).build();
        }
    }

    // 매장 상세 조회 메소드
    public StoreDetailResponseDto getStoreDetails(Long storeId, UserDetailsImpl userDetails) {

        // store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // openTimeToday
        OpenTimeResponseDto openTimeToday = getOpenTime(storeId);

        // urlList
        List<StoreDetailUrlDto> urlList = getUrlList(storeId);

        // myLike
        boolean myLike = false;

        if(userDetails != null){
            if(storeLikeRepository.findByStoreAndUser(store, userDetails.getUser()) != null){
                myLike = true;
            }
        }

        //menuList
        List<StoreDetailMenuDto> menuList = new ArrayList<>();
        List<CakeMenu> foundMenuList = cakeMenuRepository.findAllByStore_StoreId(storeId);
        for(CakeMenu menu : foundMenuList){
            StoreDetailMenuDto menuDto = new StoreDetailMenuDto(menu);
            menuList.add(menuDto);
        }

        StoreMoreDetailsDto moreDetails = orderFormService.getMoreDetails(storeId);

        //cakeImgList 최근 9개
        List<StoreDetailCakeResponseDto> cakeImgList = new ArrayList<>();
        List<Cake> foundCakeList = cakeRepository.findTop9ByStoreOrderByCreatedAtDesc(store);

        for (Cake cake : foundCakeList) {
            boolean myCakeLike = false;
            if (userDetails != null){
                User user = userDetails.getUser();
                if (cakeLikeRepository.findByUserAndCake(user, cake).isPresent()) {
                    myCakeLike = true;
                }
            }
            StoreDetailCakeResponseDto cakeDto = new StoreDetailCakeResponseDto(cake, myCakeLike);
            cakeImgList.add(cakeDto);
        }

        StoreDetailResponseDto responseDto = StoreDetailResponseDto.builder()
                .store(store)
                .openTimeToday(openTimeToday)
                .urlList(urlList)
                .myLike(myLike)
                .likeCnt(store.getLikeCnt())
                .menuList(menuList)
                .moreDetails(moreDetails)
                .cakeImgList(cakeImgList)
                .build();
        return responseDto;
    }

    // (매장 상세) 케이크 조회 메소드
    public List<StoreDetailCakeResponseDto> getCakeListAtStore(long storeId, UserDetailsImpl userDetails/*, int page*/) {

        List<Cake> foundCakeList = cakeRepository.findAllByStore_StoreId(storeId);
        /*
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"), new Sort.Order(Sort.Direction.DESC,"cakeId"));
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Cake> foundCakeList = cakeRepository.findAllByStore_StoreId(storeId, pageable);

         */


        List<StoreDetailCakeResponseDto> responseDtoList = new ArrayList<>();

        //이 아래부분 겹치는 코드 - 코드 리팩토링 필요
        for(Cake cake : foundCakeList){
            boolean myCakeLike = false;
            if(userDetails != null){
                User user = userDetails.getUser();
                if(cakeLikeRepository.findByUserAndCake(user, cake).isPresent()){
                    myCakeLike = true;
                }
            }
            //dto 값 담고, 리스트에 추가하기
            StoreDetailCakeResponseDto cakeDto = new StoreDetailCakeResponseDto(cake, myCakeLike);
            responseDtoList.add(cakeDto);
        }
        return responseDtoList;
    }

    // (매장 상세) 리뷰 조회 메소드
    public List<ReviewResponseDto> getReviewListAtStore(Long storeId, int page){
        //5개 씩 페이징
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"), new Sort.Order(Sort.Direction.DESC,"reviewId"));
        Pageable pageable = PageRequest.of(page, 5, sort);
        Page<Review> foundReviewList = reviewRepository.findAllByStore_StoreId(storeId, pageable);

        List<ReviewResponseDto> responseDtoList = new ArrayList<>();

        for(Review review : foundReviewList){
            // 리뷰 이미지 리스트 반환
            List<String> reviewImgList = new ArrayList<>();

            List<ReviewImg> rawReviewImgList = reviewImgRepository.findAllByReview_ReviewId(review.getReviewId());

            for (ReviewImg rawReviewImg : rawReviewImgList) {
                reviewImgList.add(rawReviewImg.getImgUrl());
            }

            ReviewResponseDto responseDto = ReviewResponseDto.builder()
                    .review(review)
                    .reviewImgList(reviewImgList)
                    .build();

            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 매장 검색 결과 반환 메소드
    public List<SearchResponseDto> getStoreList(SearchRequestDto requestDto) throws IOException {
        String searchType = requestDto.getSearchType();
        String searchText = requestDto.getSearchText();
        List<Store> foundStoreList = new ArrayList<>();

        if (searchType.equals("store")) {
                foundStoreList = storeRepository.findAllByNameContainingOrderByLikeCntDesc(searchText);
        } else if (searchType.equals("address")) {
                foundStoreList = storeRepository.findByFullAddressContainingOrderByLikeCntDesc(searchText);
        } else { //플레이스로 검색하기
            float minX = 0;
            float maxX = 0;
            float minY = 0;
            float maxY = 0;

            // 네이버 지도 검색 api url
            String urlString = "https://map.naver.com/v5/api/search?caller=pcweb&query=" + URLEncoder.encode("서울" + searchText, "UTF-8") + "&type=all&searchCoord=127.0234346;37.4979517&page=1&displayCount=20&isPlaceRecommendationReplace=true&lang=ko";
            JsonElement element = openApiService.getOpenApiResult(urlString);

            JsonArray rawJsonArray = element.getAsJsonObject().get("result").getAsJsonObject().get("place").getAsJsonObject().get("boundary").getAsJsonArray();

            List<Float> foundBoundaryList = new ArrayList<>();

            for (int i=0; i< rawJsonArray.size(); i++) {
                String measureString = rawJsonArray.get(i).getAsString();
                float measure = Float.parseFloat(measureString);
                foundBoundaryList.add(measure);
            }

            Collections.sort(foundBoundaryList);

            minY = foundBoundaryList.get(0) - 0.01f;
            maxY = foundBoundaryList.get(1) + 0.01f;
            minX = foundBoundaryList.get(2)- 0.01f;
            maxX = foundBoundaryList.get(3) + 0.01f;

            foundStoreList = storeRepository.findByXBetweenAndYBetweenOrderByLikeCntDesc(minX, maxX, minY, maxY);
        }

        // Dto에 담아 반환
        List<SearchResponseDto> responseDtoList = new ArrayList<>();

        for(Store store : foundStoreList){
            responseDtoList.add(StoreDetailsAtSearch(store));
        }
        return responseDtoList;
    }

    // **미완 매장 삭제 메소드
    @Transactional
    public void deleteStore(Long storeId) {
        menuRepository.deleteAllByStore_StoreId(storeId);
        storeUrlRepository.deleteAllByStore_StoreId(storeId);
        cakeRepository.deleteAllByStore_StoreId(storeId);
        openTimeRepository.deleteAllByStore_StoreId(storeId);
        storeRepository.deleteById(storeId);
    }

    // (매장 상세 페이지 조회 메소드) 매장 홈페이지 url 2개 반환 메소드 (리팩토링 매우 필요)
    public List<StoreDetailUrlDto> getUrlList(long storeId){
        List<StoreDetailUrlDto> urlList = new ArrayList<>();
        List<StoreUrl> foundUrlList = storeUrlRepository.findAllByStore_StoreId(storeId);

        if (foundUrlList.size() > 2) {
            List<StoreUrl> chosenList = foundUrlList.stream()
                    .filter(eachUrl -> eachUrl.getType().equals("normal") || eachUrl.getType().equals("instagram"))
                    .collect(Collectors.toList());

            while (chosenList.size() < 2){
                for(int i=0; i < foundUrlList.size(); i++){
                    StoreUrl eachUrl = foundUrlList.get(i);
                    if(!eachUrl.getType().equals("normal") && !eachUrl.getType().equals("instagram")){
                        chosenList.add(eachUrl);
                    }
                }
            }
            for (StoreUrl url : chosenList) {
                StoreDetailUrlDto urlDto = new StoreDetailUrlDto();
                urlDto.setUrl(url.getUrl());
                urlDto.setType(url.getType());
                urlList.add(urlDto);
            }
        }
        else {
            for (StoreUrl url : foundUrlList) {
                StoreDetailUrlDto urlDto = new StoreDetailUrlDto();
                urlDto.setUrl(url.getUrl());
                urlDto.setType(url.getType());
                urlList.add(urlDto);
            }
        }
        return urlList;
    }

    // (매장 상세 페이지 조회 메소드) 오늘의 영업 시간 조회 메소드
    public OpenTimeResponseDto getOpenTime(long storeId){

        //요일 반환
        LocalDate now = LocalDate.now();
        String dayOfWeek = "";
        String dayOfWeek2 = "";
        switch (now.getDayOfWeek().getValue()) {
            case 1:
                dayOfWeek = "월요일";
                dayOfWeek2 = "평일";
                break;
            case 2:
                dayOfWeek = "화요일";
                dayOfWeek2 = "평일";
                break;
            case 3:
                dayOfWeek = "수요일";
                dayOfWeek2 = "평일";
                break;
            case 4:
                dayOfWeek = "목요일";
                dayOfWeek2 = "평일";
                break;
            case 5:
                dayOfWeek = "금요일";
                dayOfWeek2 = "평일";
                break;
            case 6:
                dayOfWeek = "토요일";
                dayOfWeek2 = "주말";
                break;
            case 7:
                dayOfWeek = "일요일";
                dayOfWeek2 = "주말";
                break;
            default:
                dayOfWeek = "확인되지 않음";
                dayOfWeek2 = "확인되지 않음";
                break;
        }

        //storeId 로 openTime 가져오기
        List<OpenTime> openTimeList = openTimeRepository.findAllByStore_StoreId(storeId);
        if (openTimeList.size() == 0) {
            return new OpenTimeResponseDto("영업 시간은 매장 홈페이지를 확인해주세요");
        } else {
            //"화요일"이 리스트에 있는지 확인
            for (int i=0; i<openTimeList.size(); i++) {
                if (openTimeList.get(i).getType().equals(dayOfWeek)) {
                    return new OpenTimeResponseDto(openTimeList.get(i));
                }
            }
            //"매일"이 리스트에 있는지 확인
            for (int i=0; i<openTimeList.size(); i++) {
                if (openTimeList.get(i).getType().equals("매일")) {
                    return new OpenTimeResponseDto(openTimeList.get(i));
                }
            }
            //"평일" 또는 주말 이 리스트에 있는지 확인
            for (int i=0; i<openTimeList.size(); i++) {
                if (openTimeList.get(i).getType().equals(dayOfWeek2)) {
                    return new OpenTimeResponseDto(openTimeList.get(i));
                }
            }
            return new OpenTimeResponseDto("오늘은 휴일입니다");
        }
    }

    public SearchResponseDto getStoreDetailsAtSearch(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new CustomException(ErrorCode.STORE_NOT_FOUND));
        return StoreDetailsAtSearch(store);
    }

    // 내부 메소드
    public SearchResponseDto StoreDetailsAtSearch(Store store){
        String addressSimple = "";

        //"서울 OO구 OO동"
        if(!store.getFullAddress().equals(null)){
            String[] arr = store.getFullAddress().split(" ");
            addressSimple = arr[0].substring(0,2) + " "  + arr[1] + " " + arr[2];
        }

        SearchResponseDto responseDto = SearchResponseDto.builder()
                .store(store)
                .addressSimple(addressSimple)
                .build();

        return responseDto;
    }
}
