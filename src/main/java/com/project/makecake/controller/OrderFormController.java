package com.project.makecake.controller;

import com.project.makecake.dto.orders.OrderFormDetailResponseDto;
import com.project.makecake.dto.orders.OrderFormReadyResponseDto;
import com.project.makecake.dto.orders.OrderFormRequestDto;
import com.project.makecake.dto.backoffice.OrderFormPeekResponseDto;
import com.project.makecake.service.backoffice.OrderFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderFormController {
    private final OrderFormService orderFormService;

    // (백오피스) 주문서 양식 데이터 등록 시 미리보기 API
    @PostMapping("/back-office/order-forms/peek")
    public OrderFormPeekResponseDto peekOrderForm(@RequestBody OrderFormRequestDto requestDto){
       return orderFormService.peekOrderForm(requestDto);
    }

    // (백오피스) 주문서 양식 데이터 저장 API
    @PostMapping("/back-office/order-forms/add")
    public String addOrderForm(@RequestBody OrderFormRequestDto requestDto){
        return orderFormService.addOrderForm(requestDto);
    }


    // (백오피스) 주문서 양식 삭제 API
    @DeleteMapping("/back-office/order-forms/{orderFormId}")
    public String deleteOrderForm(@PathVariable long orderFormId){
        return orderFormService.deleteOrderForm(orderFormId);
    }

    // (주문하기) 주문 가능 매장 리스트 조회 API
    @GetMapping("/api/orders/stores")
    public List<OrderFormReadyResponseDto> getOrderFormList(){
        return orderFormService.getOrderFormList();
    }

    // (주문하기) 케이크 주문서 작성 페이지 조회 API
    @GetMapping("/order-forms/{orderFormId}")
    public OrderFormDetailResponseDto getOrderFormDetails(
            @PathVariable Long orderFormId
    ) {
        return orderFormService.getOrderFormDetails(orderFormId);
    }


}
