package com.project.makecake.service.backoffice;

import com.project.makecake.dto.backoffice.OrderFormPeekResponseDto;
import com.project.makecake.dto.orders.OrderFormRequestDto;
import com.project.makecake.exceptionhandler.CustomException;
import com.project.makecake.exceptionhandler.ErrorCode;
import com.project.makecake.model.OrderForm;
import com.project.makecake.model.Store;
import com.project.makecake.repository.OrderFormRepository;
import com.project.makecake.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BOOrderFormService {
    private final StoreRepository storeRepository;
    private final OrderFormRepository orderFormRepository;

    // 주문서 등록 전 데이터 미리보기 메소드
    public OrderFormPeekResponseDto peekOrderForm(OrderFormRequestDto requestDto) {
        long storeId = requestDto.getStoreId();

        // 매장명
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new CustomException(ErrorCode.STORE_NOT_FOUND));
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

    // 주문서 등록 메소드
    @Transactional
    public String addOrderForm(OrderFormRequestDto requestDto) {
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(()-> new CustomException(ErrorCode.STORE_NOT_FOUND));

        OrderForm orderForm = OrderForm.builder()
                .requestDto(requestDto)
                .store(store)
                .build();

        orderFormRepository.save(orderForm);

        return "주문서 등록 완료";
    }

    // 주문서 삭제 메소드
    @Transactional
    public String deleteOrderForm(long orderFormId) {
        OrderForm orderForm = orderFormRepository.findById(orderFormId)
                .orElseThrow(()-> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        orderFormRepository.delete(orderForm);

        return "주문서 삭제 완료";
    }
}
