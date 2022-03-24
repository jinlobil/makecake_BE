package com.project.makecake.service.backoffice;

import com.project.makecake.dto.OrderFormRequestDto;
import com.project.makecake.dto.backoffice.CakeMenuRowDto;
import com.project.makecake.dto.backoffice.CakeOptionRowDto;
import com.project.makecake.dto.backoffice.OrderFormPeekResponseDto;
import com.project.makecake.model.OrderForm;
import com.project.makecake.model.Store;
import com.project.makecake.repository.OrderFormRepository;
import com.project.makecake.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
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

    @Transactional
    public String deleteOrderForm(long orderFormId) {
        OrderForm orderForm = orderFormRepository.findById(orderFormId)
                .orElseThrow(()-> new NullPointerException("삭제하려는 주문서가 존재하지 않습니다."));

        orderFormRepository.delete(orderForm);

        return "주문서 삭제 완료";
    }

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
                .peekFormList(peekFormList)
                .peekInstructionList(peekInstructionList)
                .build();

       return responseDto;
    }
}
