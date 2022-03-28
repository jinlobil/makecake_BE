package com.project.makecake.service.backoffice;

import com.project.makecake.dto.backoffice.*;
import com.project.makecake.enums.CakePriceState;
import com.project.makecake.exceptionhandler.CustomException;
import com.project.makecake.exceptionhandler.ErrorCode;
import com.project.makecake.model.CakeMenu;
import com.project.makecake.model.Store;
import com.project.makecake.model.StoreOption;
import com.project.makecake.repository.CakeMenuRepository;
import com.project.makecake.repository.StoreOptionRepository;
import com.project.makecake.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BackOfficeService {
    private final StoreRepository storeRepository;
    private final CakeMenuRepository cakeMenuRepository;
    private final StoreOptionRepository storeOptionRepository;

    @Transactional
    public CakeMenuOptionPeekResponseDto peekMenuAndOption(CakeMenuOptionPeekRequestDto requestDto) {
        //매장 Id
        Long storeId = requestDto.getStoreId();
        System.out.println("storeId:" + storeId);

        //매장 이름
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new CustomException(ErrorCode.STORE_NOT_FOUND));
        String storeName = store.getName();

        //케이크
        List<CakeMenuRowDto> peekMenuList = new ArrayList<>();
        String aboutCake = requestDto.getAboutCake();
        List<String> rawMenuList = Arrays.asList(aboutCake.split("/")); //trim 안 된 20개

        for(int j=0; j < rawMenuList.size()/5; j++){
            List<String> rawRow = rawMenuList.subList(j*5, (j+1)*5);
            CakeMenuRowDto menuRow = new CakeMenuRowDto(rawRow.get(0).trim(), rawRow.get(1).trim(), rawRow.get(2).trim(), rawRow.get(3).trim(), rawRow.get(4).trim());
            peekMenuList.add(menuRow);
        }

        //옵션
        List<CakeOptionRowDto> peekOptionList = new ArrayList<>();
        String aboutOption = requestDto.getAboutOption();
        List<String> rawOptionList = Arrays.asList(aboutOption.split("/"));

        for(int j=0; j < rawOptionList.size()/5; j++){
            List<String> rawRow = rawOptionList.subList(j*5, (j+1)*5);
            CakeOptionRowDto optionRow = new CakeOptionRowDto(rawRow.get(0).trim(), rawRow.get(1).trim(), rawRow.get(2).trim(), rawRow.get(3).trim(), rawRow.get(4).trim());
            peekOptionList.add(optionRow);
        }

        return new CakeMenuOptionPeekResponseDto(storeId, storeName, peekMenuList, peekOptionList);
    }

    //케이크 메뉴, 옵션 데이터 저장
    public String addMenuAndOption(CakeMenuOptionPeekResponseDto requestDto) {
        Store store = storeRepository.getById(requestDto.getStoreId());

        //케이크 메뉴 저장
        List<CakeMenuRowDto> menuList = requestDto.getPeekMenuList();
        for(int i=0; i< menuList.size(); i++){
            CakeMenuRowDto menuRowDto = menuList.get(i);
            String stateValue = menuRowDto.getPriceState();
            CakeMenu cakeMenu = new CakeMenu(menuRowDto, store, CakePriceState.valueOf(stateValue));
            cakeMenuRepository.save(cakeMenu);
        }

        //케이크 옵션 저장
        List<CakeOptionRowDto> optionList = requestDto.getPeekOptionList();
        for(int i=0; i< optionList.size(); i++){
            CakeOptionRowDto optionRowDto = optionList.get(i);
            String stateValue = optionRowDto.getPriceState();
            StoreOption storeOption = new StoreOption(optionRowDto, store, CakePriceState.valueOf(stateValue));
            storeOptionRepository.save(storeOption);
        }

        return "데이터가 저장되었습니다.";
    }

    // 케이크 메뉴 삭제
    public void deleteCakeMenu(long storeId){
        cakeMenuRepository.deleteAllByStore_StoreId(storeId);
    }

    // 케이크 옵션 삭제
    public void deleteCakeOption(long storeId){
        storeOptionRepository.deleteAllByStore_StoreId(storeId);
    }


    //매장 이름으로 매장 id 검색하기
    public BoSearchStoreIdResponseDto boSearchStoreId(BoSearchStoreIdRequestDto requestDto) {
        String searchText = requestDto.getSearchText();
        Long storeId = Long.valueOf(0);
        String storeName = "";
        List<Store> searchResult = storeRepository.findByNameStartingWith(searchText);
        if(searchResult.size()>0){
            storeName = searchResult.get(0).getName();
            storeId = searchResult.get(0).getStoreId();
        }
        BoSearchStoreIdResponseDto responseDto = new BoSearchStoreIdResponseDto();
        responseDto.setStoreId(storeId);
        responseDto.setStoreName(storeName);

        return responseDto;
    }
}
