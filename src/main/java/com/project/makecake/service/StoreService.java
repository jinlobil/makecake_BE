package com.project.makecake.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.project.makecake.MakeCakeApplication;
import com.project.makecake.dto.*;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import com.project.makecake.requestDto.LikeDto;
import com.project.makecake.responseDto.CakeResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.time.LocalDate;
import java.util.*;
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

    public OpenTimeResponseDto getOpenTimeToday(long storeId){
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
        System.out.println(dayOfWeek);

        //storeId 로 openTime 가져오기
        List<OpenTime> openTimeList = openTimeRepository.findAllByStore_StoreId(storeId);
        if(openTimeList.size() == 0) {
            return new OpenTimeResponseDto("영업 시간은 매장 홈페이지를 확인해주세요");
        }else{
            //"화요일"이 리스트에 있는지 확인
            for(int i=0; i<openTimeList.size(); i++){
                if(openTimeList.get(i).getType().equals(dayOfWeek)){
                    return new OpenTimeResponseDto(openTimeList.get(i));
                }
            }
            //"매일"이 리스트에 있는지 확인
            for(int i=0; i<openTimeList.size(); i++){
                if(openTimeList.get(i).getType().equals("매일")){
                    return new OpenTimeResponseDto(openTimeList.get(i));
                }
            }
            //"평일" 또는 주말 이 리스트에 있는지 확인
            for(int i=0; i<openTimeList.size(); i++){
                if(openTimeList.get(i).getType().equals(dayOfWeek2)){
                    return new OpenTimeResponseDto(openTimeList.get(i));
                }
            }
            return new OpenTimeResponseDto("오늘은 휴일입니다");
        }
    }

    public static void main(String[] args) {
        String[] data = {"138", "129", "142"};  // 이미 투구수 데이터 배열이 있다.
        List<String> myList = new ArrayList<>(Arrays.asList(data));
        List<String> result = myList.stream()
                .filter(a -> a.equals("138") || a.equals("142"))
                .collect(Collectors.toList());
        System.out.println(result.toString());


    }

    //리팩토링 매우 필요..
    public List<StoreDetailUrlDto> getDetailUrl(long storeId){
        List<StoreDetailUrlDto> urls = new ArrayList<>();
        List<StoreUrl> rawUrlList = storeUrlRepository.findAllByStore_StoreId(storeId);

        if(rawUrlList.size() > 2) {
            List<StoreUrl> chosenList = rawUrlList.stream()
                    .filter(eachUrl -> eachUrl.getType().equals("normal") || eachUrl.getType().equals("instagram"))
                    .collect(Collectors.toList());

            while(chosenList.size() < 2){
                for(int i=0; i < rawUrlList.size(); i++){
                    StoreUrl eachUrl = rawUrlList.get(i);
                    if(!eachUrl.getType().equals("normal") && !eachUrl.getType().equals("instagram")){
                        chosenList.add(eachUrl);
                    }
                }
            }
            for(StoreUrl rawUrl : chosenList){
                StoreDetailUrlDto urlDto = new StoreDetailUrlDto();
                urlDto.setUrl(rawUrl.getUrl());
                urlDto.setType(rawUrl.getType());
                urls.add(urlDto);
            }
        }
        else{
            for(StoreUrl rawUrl : rawUrlList){
                StoreDetailUrlDto urlDto = new StoreDetailUrlDto();
                urlDto.setUrl(rawUrl.getUrl());
                urlDto.setType(rawUrl.getType());
                urls.add(urlDto);
            }
        }
        return urls;
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
    public LikeDto likeStore(Boolean myLike, Long storeId, User user) {
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
            return new LikeDto(true);
        } else {
            storeLikeRepository.deleteByStoreAndUser(store, user);
            store.setLikeCnt(store.getLikeCnt() -1);
            storeRepository.save(store);
            return new LikeDto(false);
        }
    }

    //매장 상세
    @Transactional
    public StoreDetailResponseDto getStoreDetail(Long storeId, UserDetailsImpl userDetails) {

        //store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new IllegalArgumentException("매장 정보가 없습니다."));

        //openTimeToday
        OpenTimeResponseDto openTimeToday = getOpenTimeToday(storeId);

        //urls
        List<StoreDetailUrlDto> urls = getDetailUrl(storeId);

        //myLike
        Boolean myLike = false;

        if(userDetails != null){
            if(storeLikeRepository.findByStoreAndUser(store, userDetails.getUser()) != null){
                myLike = true;
            }
        }

        //menus
        List<StoreDetailMenuDto> menus = new ArrayList<>();
        List<CakeMenu> rawMenuList = cakeMenuRepository.findAllByStore_StoreId(storeId);
        for(CakeMenu rawMenu : rawMenuList){
            StoreDetailMenuDto menuDto = new StoreDetailMenuDto(rawMenu);
            menus.add(menuDto);
        }

        //cakeImages 최근 9개
        List<StoreDetailCakeResponseDto> cakeImages = new ArrayList<>();
        List<Cake> rawCakeList = cakeRepository.findTop9ByStoreOrderByCreatedAtDesc(store);
        for(Cake rawCake : rawCakeList){

            Boolean myCakeLike = false;
            if(userDetails != null){
                User user = userDetails.getUser();
                if(cakeLikeRepository.findByUserAndCake(user, rawCake).isPresent()){
                    myCakeLike = true;
                }
            }
            StoreDetailCakeResponseDto cakeDto = new StoreDetailCakeResponseDto(rawCake,myCakeLike);
            cakeImages.add(cakeDto);
        }
        //Store store, String openTimeToday, List<StoreDetailUrlDto> urls,
        //Boolean myLike,List<StoreDetailMenuDto> menus, List<StoreDetailCakeResponseDto> cakeImages

        return new StoreDetailResponseDto(store, openTimeToday, urls, myLike, menus, cakeImages);
    }



    //매장 상세정보- 케이크
    @Transactional
    public List<StoreDetailCakeResponseDto> getStoreDetailCakes(Long storeId, UserDetailsImpl userDetails, int page) {
        //일단 15개씩 페이징
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"), new Sort.Order(Sort.Direction.DESC,"cakeId"));
        Pageable pageable = PageRequest.of(page, 15, sort);
        Page<Cake> foundCakeList = cakeRepository.findAllByStore_StoreId(storeId, pageable);

        List<StoreDetailCakeResponseDto> responseDto = new ArrayList<>();

        //이 아래부분 겹치는 코드 - 코드 리팩토링 필요
        for(Cake rawCake : foundCakeList){
            Boolean myCakeLike = false;
            if(userDetails != null){
                User user = userDetails.getUser();
                if(cakeLikeRepository.findByUserAndCake(user, rawCake).isPresent()){
                    myCakeLike = true;
                }
            }

            //dto 값 담고, 리스트에 추가하기
            StoreDetailCakeResponseDto cakeDto = new StoreDetailCakeResponseDto(rawCake, myCakeLike);
            responseDto.add(cakeDto);
        }
        return responseDto;
    }

    //매장 상세정보 - 리뷰
    @Transactional
    public List<ReviewResponseDto> getStoreDetailReviews(Long storeId,int page){
        //5개 씩 페이징
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"), new Sort.Order(Sort.Direction.DESC,"reviewId"));
        Pageable pageable = PageRequest.of(page, 5, sort);
        Page<Review> foundReviewList = reviewRepository.findAllByStore_StoreId(storeId, pageable);


        List<ReviewResponseDto> responseDto = new ArrayList<>();
        Store store = storeRepository.getById(storeId);

        //이 부분 무한 스크롤로 구현 시 수정 필요함
        List<Review> rawReviewList = reviewRepository.findTop3ByStoreOrderByCreatedAtDesc(store);

        //이 아래부분 겹치는 코드 - 코드 리팩토링 필요
        for(Review rawReview : rawReviewList){

            // 리뷰 이미지 리스트 반환
            List<String> reviewImages = new ArrayList<>();
            List<ReviewImg> rawReviewImgList = reviewImgRepository.findAllByReview_ReviewId(rawReview.getReviewId());
            for(ReviewImg rawReviewImg : rawReviewImgList){
                reviewImages.add(rawReviewImg.getImgUrl());
            }
            ReviewResponseDto reviewDto = new ReviewResponseDto(rawReview, reviewImages);

            responseDto.add(reviewDto);
        }
        return responseDto;
    }

    //매장 검색하기
    @Transactional
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
            String addressSimple = "";

            //"서울 OO구 OO동"
            if(!rawStore.getFullAddress().equals(null)){
                String[] arr = rawStore.getFullAddress().split(" ");
                addressSimple = arr[0].substring(0,2) + " "  + arr[1] + " " + arr[2];
            }
            SearchResponseDto responseDto = new SearchResponseDto(rawStore, addressSimple);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }


    //매장 삭제(미완)
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
