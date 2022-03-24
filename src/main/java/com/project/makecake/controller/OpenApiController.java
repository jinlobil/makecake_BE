package com.project.makecake.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.project.makecake.dto.OpenApiImgDto;
import com.project.makecake.dto.OpenApiStoreUrlDto;
import com.project.makecake.model.Cake;
import com.project.makecake.model.Store;
import com.project.makecake.repository.CakeRepository;
import com.project.makecake.repository.StoreRepository;
import com.project.makecake.service.OpenApiService;
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
public class OpenApiController {
    private final StoreRepository storeRepository;
    private final CakeRepository cakeRepository;
    private final OpenApiService openApiService;

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

            //[{storeName : 찹챱케이크, imageList: [url, url, url, url]}, {name : 두두케이크, images: [url, url, url, url]}]

    }

    @PostMapping("/crawling")
    public void goCrawling(@RequestBody OpenApiStoreUrlDto urlResponseDto) throws IOException {
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

        //Gson 라이브러리로 JSON파싱
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);

//        JsonArray rawList = element.getAsJsonObject().get("result").getAsJsonObject().get("place").getAsJsonObject().get("list").getAsJsonArray();

        JsonArray rawList = element.getAsJsonObject().get("result").getAsJsonObject().get("place").getAsJsonObject().get("boundary").getAsJsonArray();
        for(int i =0; i < rawList.size();i++){
            System.out.println(i + "번째 " + rawList.get(i).getAsString());
        }
    }


    }
