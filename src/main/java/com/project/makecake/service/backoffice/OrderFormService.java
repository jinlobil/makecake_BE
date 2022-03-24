package com.project.makecake.service.backoffice;

import com.project.makecake.dto.OrderFormRequestDto;
import com.project.makecake.model.OrderForm;
import com.project.makecake.model.Store;
import com.project.makecake.repository.OrderFormRepository;
import com.project.makecake.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
}
