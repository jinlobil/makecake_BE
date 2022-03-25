package com.project.makecake.service.backoffice;

import com.project.makecake.dto.*;
import com.project.makecake.dto.backoffice.OrderFormPeekResponseDto;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderFormService {
    private final StoreRepository storeRepository;
    private final OrderFormRepository orderFormRepository;
    private final CakeMenuRepository cakeMenuRepository;
    private final StoreOptionRepository storeOptionRepository;


    // (백오피스) 주문서 등록 메소드
    @Transactional
    public String addOrderForm(OrderFormRequestDto requestDto) {
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(()-> new NullPointerException("등록하려는 매장이 존재하지 않습니다."));

        OrderForm orderForm = OrderForm.builder()
                .requestDto(requestDto)
                .store(store)
                .build();

        orderFormRepository.save(orderForm);

        return "주문서 등록 완료";
    }

    // (백오피스) 주문서 삭제 메소드
    @Transactional
    public String deleteOrderForm(long orderFormId) {
        OrderForm orderForm = orderFormRepository.findById(orderFormId)
                .orElseThrow(()-> new NullPointerException("삭제하려는 주문서가 존재하지 않습니다."));

        orderFormRepository.delete(orderForm);

        return "주문서 삭제 완료";
    }

    // (백오피스) 주문서 등록 전 데이터 미리보기 메소드
    public OrderFormPeekResponseDto peekOrderForm(OrderFormRequestDto requestDto) {
        long storeId = requestDto.getStoreId();

        // 매장명
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("매장 id가 DB에 없습니다."));
        String storeName = store.getName();

        // 주문서 양식
        List<String> peekFormList = new ArrayList<>();
        String stringForm = requestDto.getForm();
        List<String> rawFormList = Arrays.asList(stringForm.split(":"));

        for(String rawForm : rawFormList){
            peekFormList.add(rawForm.trim());
        }

        // 주문 전 필독사항
        List<String> peekInstructionList = new ArrayList<>();
        String stringInstruction = requestDto.getInstruction();

        List<String> rawInstructionList = Arrays.asList(stringInstruction.trim().split("\\*"));
        for(String rawInstruction : rawInstructionList) {
            if(!rawInstruction.trim().equals("")){
                peekInstructionList.add(rawInstruction.trim());
            }
        }

        OrderFormPeekResponseDto responseDto = OrderFormPeekResponseDto.builder()
                .storeId(storeId)
                .storeName(storeName)
                .name(requestDto.getName())
                .peekFormList(peekFormList)
                .peekInstructionList(peekInstructionList)
                .build();

       return responseDto;
    }

    // (주문하기) 주문 가능 매장 리스트 조회 메소드
    public List<OrderFormReadyResponseDto> getOrderFormList() {
        List<OrderFormReadyResponseDto> responseDtoList = new ArrayList<>();
        List<OrderForm> foundOrderFormList = orderFormRepository.findAll();
        for(OrderForm orderForm : foundOrderFormList){
            OrderFormReadyResponseDto responseDto = OrderFormReadyResponseDto.builder()
                    .orderForm(orderForm)
                    .build();
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }


    // (주문하기) 케이크 주문서 작성 페이지 조회 API
    public OrderFormDetailResponseDto getOrderFormDetails(Long orderFormId) {

        OrderForm orderForm = orderFormRepository.findById(orderFormId)
                .orElseThrow(()-> new NullPointerException("주문서가 존재하지 않습니다."));


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

        //cakeMenuList
        List<StoreMoreCakeMenuDto> cakeMenuList = new ArrayList<>();
        List<CakeMenu> foundMenuList = cakeMenuRepository.findAllByStore_StoreId(orderForm.getStore().getStoreId());
        for(CakeMenu menu : foundMenuList){
            StoreMoreCakeMenuDto menuDto = StoreMoreCakeMenuDto.builder()
                    .cakeMenu(menu)
                    .build();
            cakeMenuList.add(menuDto);
        }

        //cakeTasteList
        List<StoreMoreCakeTasteDto> cakeTasteList = new ArrayList<>();
        List<StoreMoreCakeOptionDto> cakeOptionList = new ArrayList<>();
        List<StoreOption> foundOptionList = storeOptionRepository.findAllByStore_StoreId(orderForm.getStore().getStoreId());
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


        OrderFormDetailResponseDto responseDto = OrderFormDetailResponseDto.builder()
                .orderForm(orderForm)
                .formList(formList)
                .instructionList(instructionList)
                .moreDetails(moreDetails)
                .build();

        return responseDto;
    }
}
