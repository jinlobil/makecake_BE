package com.project.makecake.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import com.google.gson.*;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class OpenApiService {
    private final CakeRepository cakeRepository;
    private final MenuRepository menuRepository;
    private final StoreUrlRepository storeUrlRepository;
    private final StoreRepository storeRepository;
    private final OpenTimeRepository openTimeRepository;

    public String uniToKor(String uni){
        StringBuffer result = new StringBuffer();

        for(int i=0; i<uni.length(); i++){
            if(uni.charAt(i) == '\\' &&  uni.charAt(i+1) == 'u'){
                Character c = (char)Integer.parseInt(uni.substring(i+2, i+6), 16);
                result.append(c);
                i+=5;
            }else{
                result.append(uni.charAt(i));
            }
        }
        return result.toString();
    }


    @Transactional
    public void TryCrawlingLetteringCake(int productNo) throws IOException {
        URL url = new URL( "https://map.naver.com/v5/api/sites/summary/" + productNo + "?lang=ko");

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

        long id = element.getAsJsonObject().get("id").getAsLong();
        String name = element.getAsJsonObject().get("name").getAsString();
        float x = element.getAsJsonObject().get("x").getAsFloat();
        float y = element.getAsJsonObject().get("y").getAsFloat();
        String fullAddress = element.getAsJsonObject().get("fullAddress").getAsString();
        String fullRoadAddress = element.getAsJsonObject().get("fullRoadAddress").getAsString();
        String phone = element.getAsJsonObject().get("phone").getAsString();
        String description = element.getAsJsonObject().get("description").getAsString();

        String mainImg = "";
        if(element.getAsJsonObject().get("imageURL")!= null){
            mainImg = element.getAsJsonObject().get("imageURL").getAsString();
        }

        String openTimeString = "";
        if(!element.getAsJsonObject().get("bizhourInfo").isJsonNull()){
            openTimeString = element.getAsJsonObject().get("bizhourInfo").getAsString();
        }

        Store store = new Store();
        store.setNaverId(id);
        store.setName(name);
        store.setX(x);
        store.setY(y);
        store.setFullAddress(fullAddress);
        store.setRoadAddress(fullRoadAddress);
        store.setMainImg(mainImg);
        store.setDescription(description);
        store.setPhone(phone);
        store.setOpenTimeString(openTimeString);

        storeRepository.save(store);


        JsonArray arr = element.getAsJsonObject().get("urlList").getAsJsonArray();
        for (int i=0; i<arr.size();i++) {
            String type = arr.get(i).getAsJsonObject().get("type").getAsString();
            String addUrl = arr.get(i).getAsJsonObject().get("url").getAsString();

            StoreUrl storeUrl = new StoreUrl();
            storeUrl.setUrl(addUrl);
            storeUrl.setType(type);
            storeUrl.setStore(store);

            storeUrlRepository.save(storeUrl);
        }


        if(!element.getAsJsonObject().get("menus").isJsonNull()){
            JsonArray menus = element.getAsJsonObject().get("menus").getAsJsonArray();

            for(int i = 0; i < menus.size(); i++){
                String names = menus.get(i).getAsJsonObject().get("name").getAsString();
                String price = menus.get(i).getAsJsonObject().get("price").getAsString();
                boolean change = menus.get(i).getAsJsonObject().get("change").getAsBoolean();

                Menu menu = new Menu();
                menu.setName(names);
                menu.setPrice(price);
                menu.setChanges(change);
                menu.setStore(store);

                menuRepository.save(menu);
            }
        }



        //cake
        JsonArray images = element.getAsJsonObject().get("images").getAsJsonArray();

        for(int i = 0; i < images.size(); i++){
            String imgUrl = images.get(i).getAsJsonObject().get("url").getAsString();
            Cake cake = new Cake(imgUrl,store);

            cakeRepository.save(cake);
        }

        JsonArray bizhourList = null;
        if(!element.getAsJsonObject().get("bizHour").isJsonNull()) {
            bizhourList = element.getAsJsonObject().get("bizHour").getAsJsonArray();

            for(int i=0; i < bizhourList.size(); i++){
                String type = bizhourList.get(i).getAsJsonObject().get("type").getAsString();
                String startTime = bizhourList.get(i).getAsJsonObject().get("startTime").getAsString();
                String endTime = bizhourList.get(i).getAsJsonObject().get("endTime").getAsString();
                String descriptionTime = bizhourList.get(i).getAsJsonObject().get("description").getAsString();
                Boolean isDayOff = bizhourList.get(i).getAsJsonObject().get("isDayOff").getAsBoolean();

                OpenTime openTime = new OpenTime();
                openTime.setType(type);
                openTime.setStartTime(startTime);
                openTime.setEndTime(endTime);
                openTime.setDescriptionTime(descriptionTime);
                openTime.setIsDayOff(isDayOff);
                openTime.setStore(store);

                openTimeRepository.save(openTime);
            }
        }
















        br.close();
    }

}