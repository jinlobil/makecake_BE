package com.project.makecake.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.project.makecake.dto.*;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public JsonElement CrawlingSearch(String searchText) throws IOException {
        URL url = new URL( "https://map.naver.com/v5/api/search?caller=pcweb&query=" + URLEncoder.encode(searchText, "UTF-8") + "&type=all&searchCoord=127.0234346;37.4979517&page=1&displayCount=20&isPlaceRecommendationReplace=true&lang=ko");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setDoOutput(true);

        //결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode : " + responseCode);

        //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }

        //Gson 라이브러리로 JSON파싱
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);
        return element;
    }

    //홈탭 : 핫 매장 리스트
    @Transactional
    public List<HomeStoreDto> getHomeStoreList() {
        List<HomeStoreDto> homeStoreDtoList = new ArrayList<>();

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

    //매장 좋아요
    @Transactional
    public Boolean likeStore(Boolean myLike, Long storeId, User user) {
        //true 추가(좋아요 누르기), false 삭제(좋아요 취소)
        Store store = storeRepository.getById(storeId);

        if (myLike) {
            //storeLike에 추가하기 (user 구현 완료 시 넣기)
            StoreLike storeLike = new StoreLike();
            storeLike.setStore(store);
            storeLike.setUser(user);
            storeLikeRepository.save(storeLike);
            store.setLikeCnt(store.getLikeCnt() +1);
            storeRepository.save(store);
            return true;
        } else {
            storeLikeRepository.deleteByStoreAndUser(store, user);
            store.setLikeCnt(store.getLikeCnt() -1);
            storeRepository.save(store);
            return false;
        }
    }

    //매장 상세
    @Transactional
    public StoreDetailResponseDto getStoreDetail(Long storeId, UserDetailsImpl userDetails) {
        StoreDetailResponseDto responseDto = new StoreDetailResponseDto();
        Store store = storeRepository.findById(storeId).get();
        responseDto.setStoreId(store.getStoreId());
        responseDto.setMainImg(store.getMainImg());
        responseDto.setName(store.getName());
        responseDto.setRoadAddress(store.getRoadAddress());
        responseDto.setFullAddress(store.getFullAddress());
        responseDto.setDescription(store.getDescription());
        responseDto.setOpenTimeString(store.getOpenTimeString());
        responseDto.setLikeCnt(store.getLikeCnt());

        Boolean myLike = false;

        System.out.println("마이라이크");
        if(userDetails != null){
            if(storeLikeRepository.findByStoreAndUser(store, userDetails.getUser()) != null){
                myLike = true;
            }
        }
        responseDto.setMyLike(myLike);

        //urls
        List<StoreDetailUrlDto> urls = new ArrayList<>();
        List<StoreUrl> rawUrlList = storeUrlRepository.findAllByStore_StoreId(storeId);
        for(StoreUrl rawUrl : rawUrlList){
            StoreDetailUrlDto urlDto = new StoreDetailUrlDto();
            urlDto.setUrl(rawUrl.getUrl());
            urlDto.setType(rawUrl.getType());
            urls.add(urlDto);
        }
        responseDto.setUrls(urls);

        //menus
        List<StoreDetailMenuDto> menus = new ArrayList<>();
        List<Menu> rawMenuList = menuRepository.findAllByStore_StoreId(storeId);
        for(Menu rawMenu : rawMenuList){
            StoreDetailMenuDto menuDto = new StoreDetailMenuDto();
            menuDto.setName(rawMenu.getName());
            menuDto.setPrice(rawMenu.getPrice());
            menuDto.setChanges(rawMenu.getChanges());
            menus.add(menuDto);
        }
        responseDto.setMenus(menus);

        //reviews 최근 3개만
        List<ReviewResponseDto> reviews = new ArrayList<>();
        List<Review> rawReviewList = reviewRepository.findTop3ByStoreOrderByCreatedAtDesc(store);
        for(Review rawReview : rawReviewList){
            ReviewResponseDto reviewDto = new ReviewResponseDto();
            long reviewId = rawReview.getReviewId();
            reviewDto.setReviewId(reviewId);
            reviewDto.setWriterNickname(rawReview.getUser().getNickname());
            reviewDto.setCreatedDate(rawReview.getCreatedAt());
            reviewDto.setContent(rawReview.getContent());

            List<String> reviewImages = new ArrayList<>();
            List<ReviewImg> rawReviewImgList = reviewImgRepository.findAllByReview_ReviewId(reviewId);
            for(ReviewImg rawReviewImg : rawReviewImgList){
                reviewImages.add(rawReviewImg.getImgUrl());
            }

            reviewDto.setReviewImages(reviewImages);

            reviews.add(reviewDto);
        }
        responseDto.setReviews(reviews);

        //cakeImages 최근 9개만
        List<StoreDetailCakeResponseDto> cakeImages = new ArrayList<>();
        List<Cake> rawCakeList = cakeRepository.findTop9ByStoreOrderByCreatedAtDesc(store);
        for(Cake rawCake : rawCakeList){
            StoreDetailCakeResponseDto cakeDto = new StoreDetailCakeResponseDto();
            cakeDto.setCakeId(rawCake.getCakeId());
            cakeDto.setImg(rawCake.getUrl());
            cakeDto.setLikeCnt(rawCake.getLikeCnt());

            Boolean myCakeLike = false;
            if(userDetails != null){
                User user = userDetails.getUser();
                if(cakeLikeRepository.findByUserAndCake(user, rawCake).isPresent()){
                    myCakeLike = true;
                }
            }
            cakeDto.setMyLike(myCakeLike);
            cakeImages.add(cakeDto);
        }
        responseDto.setCakeImages(cakeImages);
        return responseDto;
    }

    //매장 상세정보- 케이크
    @Transactional
    public List<StoreDetailCakeResponseDto> getStoreDetailCakes(Long storeId, UserDetailsImpl userDetails) {
        List<StoreDetailCakeResponseDto> responseDto = new ArrayList<>();
        Store store = storeRepository.getById(storeId);

        //이 부분 무한 스크롤로 구현 시 수정 필요함
        List<Cake> rawCakeList = cakeRepository.findTop9ByStoreOrderByCreatedAtDesc(store);

        //이 아래부분 겹치는 코드 - 코드 리팩토링 필요
        for(Cake rawCake : rawCakeList){
            StoreDetailCakeResponseDto cakeDto = new StoreDetailCakeResponseDto();
            cakeDto.setCakeId(rawCake.getCakeId());
            cakeDto.setImg(rawCake.getUrl());
            cakeDto.setLikeCnt(rawCake.getLikeCnt());

            Boolean myCakeLike = false;
            if(userDetails != null){
                User user = userDetails.getUser();
                if(cakeLikeRepository.findByUserAndCake(user, rawCake).isPresent()){
                    myCakeLike = true;
                }
            }
            cakeDto.setMyLike(myCakeLike);
            responseDto.add(cakeDto);
        }
        return responseDto;
    }

    //매장 상세정보 - 리뷰
    @Transactional
    public List<ReviewResponseDto> getStoreDetailReviews(Long storeId){
        List<ReviewResponseDto> reviews = new ArrayList<>();
        Store store = storeRepository.getById(storeId);

        //이 부분 무한 스크롤로 구현 시 수정 필요함
        List<Review> rawReviewList = reviewRepository.findTop3ByStoreOrderByCreatedAtDesc(store);

        //이 아래부분 겹치는 코드 - 코드 리팩토링 필요
        for(Review rawReview : rawReviewList){
            ReviewResponseDto reviewDto = new ReviewResponseDto();
            long reviewId = rawReview.getReviewId();
            reviewDto.setReviewId(reviewId);
            reviewDto.setWriterNickname(rawReview.getUser().getNickname());
            reviewDto.setCreatedDate(rawReview.getCreatedAt());
            reviewDto.setContent(rawReview.getContent());

            List<String> reviewImages = new ArrayList<>();
            List<ReviewImg> rawReviewImgList = reviewImgRepository.findAllByReview_ReviewId(reviewId);
            for(ReviewImg rawReviewImg : rawReviewImgList){
                reviewImages.add(rawReviewImg.getImgUrl());
            }

            reviewDto.setReviewImages(reviewImages);

            reviews.add(reviewDto);
        }
        return reviews;
    }

    public List<SearchResponseDto> getSearchStore(SearchRequestDto requestDto) throws IOException {
        String searchType = requestDto.getSearchType();
        String sortType = requestDto.getSortType();
        String searchText = requestDto.getSearchText();
        List<Store> rawStoreList = new ArrayList<>();

        if (searchType.equals("store")) {
            if (sortType != "review") {
                rawStoreList = storeRepository.findAllByNameStartingWithOrderByLikeCntDesc(searchText);
            } else {
                rawStoreList = storeRepository.findAllByNameStartingWithOrderByReviewCntDesc(searchText);
            }
        } else if (searchType.equals("address")) {
            if (sortType != "review") {
                rawStoreList = storeRepository.findByFullAddressContainingOrderByLikeCntDesc(searchText);
            } else {
                rawStoreList = storeRepository.findByFullAddressContainingOrderByReviewCntDesc(searchText);
            }
        } else { //플레이스로 검색하기
            float minX = 0;
            float maxX = 0;
            float minY = 0;
            float maxY = 0;
            JsonElement element = CrawlingSearch(searchText);
            JsonArray rawJsonArray = element.getAsJsonObject().get("result").getAsJsonObject().get("place").getAsJsonObject().get("boundary").getAsJsonArray();
            List<Float> rawList = new ArrayList<>();
            for(int i=0; i< rawJsonArray.size(); i++){
                String measureString = rawJsonArray.get(i).getAsString();
                float measure = Float.parseFloat(measureString);
                rawList.add(measure);
            }
            Collections.sort(rawList);

            minY = rawList.get(0);
            maxY = rawList.get(1);
            minX = rawList.get(2);
            maxX = rawList.get(3);
            System.out.println(rawList.toString());

            if (sortType != "review") {
                rawStoreList = storeRepository.findByXBetweenAndYBetweenOrderByLikeCntDesc(minX, maxX, minY, maxY);
            } else {
                rawStoreList = storeRepository.findByXBetweenAndYBetweenOrderByLikeCntDesc(minX, maxX, minY, maxY);
            }
        }

        System.out.println(rawStoreList.size());

        //rawStoreList를 Dto에 담기
        List<SearchResponseDto> responseDtoList = new ArrayList<>();

        for(Store rawStore : rawStoreList){
            SearchResponseDto responseDto = new SearchResponseDto();
            responseDto.setStoreId(rawStore.getStoreId());
            responseDto.setName(rawStore.getName());
            responseDto.setX(rawStore.getX());
            responseDto.setY(rawStore.getY());
            responseDto.setRoadAddress(rawStore.getRoadAddress());
            responseDto.setFulAddress(rawStore.getFullAddress());
            responseDto.setMainImg(rawStore.getMainImg());
            responseDto.setLikeCnt(rawStore.getLikeCnt());
            responseDto.setReviewCnt(rawStore.getReviewCnt());

            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    @Transactional
    public void deleteStore(Long storeId) {
        //menu, storeUrl, cake
        menuRepository.deleteAllByStore_StoreId(storeId);
        storeUrlRepository.deleteAllByStore_StoreId(storeId);
        cakeRepository.deleteAllByStore_StoreId(storeId);
        openTimeRepository.deleteAllByStore_StoreId(storeId);
        storeRepository.deleteById(storeId);

    }
}
