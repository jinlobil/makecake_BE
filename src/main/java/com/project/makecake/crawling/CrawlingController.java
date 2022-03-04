package com.project.makecake.crawling;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.project.makecake.model.Cake;
import com.project.makecake.model.Store;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CrawlingController {
    private final StoreRepository storeRepository;
    private final CakeRepository cakeRepository;
    private final CrawlingService crawlingService;

    @GetMapping("/cake")
    public List<CrawlingImageDto> getCakes(){

        List<CrawlingImageDto> imageListResponse = new ArrayList<>();
        List<Store> storeList = storeRepository.findAll();
        for(int i = 0; i < storeList.size(); i++) {
            CrawlingImageDto crawlingImageDto = new CrawlingImageDto();
            String storeName = storeList.get(i).getName();
            List<String> imageList = new ArrayList<>();
            List<Cake> cakeList = cakeRepository.findAllByStore(storeList.get(i));
            for(int j = 0; j < cakeList.size(); j++){
                String imageUrl = cakeList.get(j).getUrl();
                imageList.add(imageUrl);
            }
            crawlingImageDto.setStoreName(storeName);
            crawlingImageDto.setImageList(imageList);

            imageListResponse.add(crawlingImageDto);
        }

        return imageListResponse;

            //[{storeName : 찹챱케이크, imageList: [url, url, url, url]}, {name : 두두케이크, images: [url, url, url, url]}]

    }

    @PostMapping("/crawling")
    public void goCrawling(@RequestBody CrawlingUrlDto urlResponseDto) throws IOException {
//        URL url = new URL( "https://map.naver.com/v5/api/search?caller=pcweb&query=%EC%84%9C%EC%9A%B8%20%EB%A0%88%ED%84%B0%EB%A7%81%20%EC%BC%80%EC%9D%B4%ED%81%AC&type=all&searchCoord=126.8039092000003;37.556616299999824&page=1&displayCount=10&isPlaceRecommendationReplace=true&lang=ko");


        URL url = new URL(urlResponseDto.getGivenUrl());

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
//        System.out.println("response body : " + result);

        //Gson 라이브러리로 JSON파싱
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);

        JsonArray rawList = element.getAsJsonObject().get("result").getAsJsonObject().get("place").getAsJsonObject().get("list").getAsJsonArray();

        int count = 0;
        for(int i = 0; i < rawList.size(); i++){
            int id = rawList.get(i).getAsJsonObject().get("id").getAsInt();
            count += 1;
            System.out.println(count + "번 째 " + "아이디 뽑아봐 " + id);
            crawlingService.TryCrawlingLetteringCake(id);
        }
    }


    }
