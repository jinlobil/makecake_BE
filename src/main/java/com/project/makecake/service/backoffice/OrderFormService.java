package com.project.makecake.service.backoffice;

import com.project.makecake.dto.orders.OrderReadyStoreResponseDto;
import com.project.makecake.dto.orders.OrderFormDetailResponseDto;
import com.project.makecake.dto.orders.OrderFormReadyResponseDto;
import com.project.makecake.dto.store.StoreMoreCakeMenuDto;
import com.project.makecake.dto.store.StoreMoreCakeOptionDto;
import com.project.makecake.dto.store.StoreMoreCakeTasteDto;
import com.project.makecake.dto.store.StoreMoreDetailsDto;
import com.project.makecake.exceptionhandler.CustomException;
import com.project.makecake.exceptionhandler.ErrorCode;
import com.project.makecake.model.CakeMenu;
import com.project.makecake.model.OrderForm;
import com.project.makecake.model.Store;
import com.project.makecake.model.StoreOption;
import com.project.makecake.repository.CakeMenuRepository;
import com.project.makecake.repository.OrderFormRepository;
import com.project.makecake.repository.StoreOptionRepository;
import com.project.makecake.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor

public class OrderFormService {
    private final OrderFormRepository orderFormRepository;
    private final CakeMenuRepository cakeMenuRepository;
    private final StoreOptionRepository storeOptionRepository;


    // 주문 가능한 주문서 리스트 조회 메소드
    public List<OrderFormReadyResponseDto> getOrderFormList() {
        List<OrderFormReadyResponseDto> responseDtoList = new ArrayList<>();
        List<OrderForm> foundOrderFormList = orderFormRepository.findAllByOrderByNameAsc();
        System.out.println(foundOrderFormList.size());
        for(OrderForm orderForm : foundOrderFormList){
            OrderFormReadyResponseDto responseDto = OrderFormReadyResponseDto.builder()
                    .orderForm(orderForm)
                    .build();
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 케이크 주문서 작성 페이지 조회 메소드
    public OrderFormDetailResponseDto getOrderFormDetails(Long orderFormId) {

        OrderForm orderForm = orderFormRepository.findById(orderFormId)
                .orElseThrow(()-> new CustomException(ErrorCode.ORDER_NOT_FOUND));


        // 주문서 양식
        List<String> formList = new ArrayList<>();
        String form = orderForm.getForm();
        List<String> rawFormList = Arrays.asList(form.split(":"));

        for(String rawForm : rawFormList){
            formList.add(rawForm.trim());
        }

        // 주문 전 필독사항
        List<String> instructionList = new ArrayList<>();
        String instruction = orderForm.getInstruction();

        List<String> rawInstructionList = Arrays.asList(instruction.trim().split("\\*"));
        for(String rawInstruction : rawInstructionList) {
            if(!rawInstruction.trim().equals("")){
                instructionList.add(rawInstruction.trim());
            }
        }

        //moreDetails
        StoreMoreDetailsDto moreDetails = getMoreDetails(orderForm.getStore().getStoreId());

        OrderFormDetailResponseDto responseDto = OrderFormDetailResponseDto.builder()
                .orderForm(orderForm)
                .formList(formList)
                .instructionList(instructionList)
                .moreDetails(moreDetails)
                .build();

        return responseDto;
    }

    // 주문 가능한 매장 정보 조회 메소드
    public List<OrderReadyStoreResponseDto> getOrderReadyStoreList() {
        List<OrderReadyStoreResponseDto> responseDtoList = new ArrayList<>();

        List<Store> foundStoreList = orderFormRepository.findDistinctStore();
        for(Store store : foundStoreList){
            String addressSimple = "";

            //"서울 OO구 OO동"
            if(!store.getFullAddress().equals(null)){
                String[] arr = store.getFullAddress().split(" ");
                addressSimple = arr[0].substring(0,2) + " "  + arr[1] + " " + arr[2];
            }

            OrderReadyStoreResponseDto responseDto = OrderReadyStoreResponseDto.builder()
                    .store(store)
                    .simpleAddress(addressSimple)
                    .build();
            responseDtoList.add(responseDto);

            // 간편 주소 가나다 순 정렬
            Comparator<OrderReadyStoreResponseDto> compareByAddress = (OrderReadyStoreResponseDto r1, OrderReadyStoreResponseDto r2) -> r1.getSimpleAddress().compareTo( r2.getSimpleAddress() );
            Collections.sort(responseDtoList, compareByAddress);

        }
        return responseDtoList;
    }

    // (내부 메소드) 케이크 메뉴, 꾸미기 옵션 조회 메소드
    public StoreMoreDetailsDto getMoreDetails(long storeId){
        //cakeMenuList
        List<StoreMoreCakeMenuDto> cakeMenuList = new ArrayList<>();
        List<CakeMenu> foundMenuList = cakeMenuRepository.findAllByStore_StoreId(storeId);
        for(CakeMenu menu : foundMenuList){
            StoreMoreCakeMenuDto menuDto = StoreMoreCakeMenuDto.builder()
                    .cakeMenu(menu)
                    .build();
            cakeMenuList.add(menuDto);
        }

        //cakeTasteList
        List<StoreMoreCakeTasteDto> cakeTasteList = new ArrayList<>();
        List<StoreMoreCakeOptionDto> cakeOptionList = new ArrayList<>();
        List<StoreOption> foundOptionList = storeOptionRepository.findAllByStore_StoreId(storeId);
        for(StoreOption storeOption : foundOptionList){

            if(storeOption.getMainCat().equals("맛")) {
                StoreMoreCakeTasteDto tasteDto = StoreMoreCakeTasteDto.builder()
                        .storeOption(storeOption)
                        .build();

                cakeTasteList.add(tasteDto);
            } else {
                StoreMoreCakeOptionDto optionDto = StoreMoreCakeOptionDto.builder()
                        .storeOption(storeOption)
                        .build();

                cakeOptionList.add(optionDto);
            }

        }

        StoreMoreDetailsDto moreDetails = StoreMoreDetailsDto.builder()
                .cakeMenuList(cakeMenuList)
                .cakeTasteList(cakeTasteList)
                .cakeOptionList(cakeOptionList)
                .build();

        return moreDetails;
    }
}
