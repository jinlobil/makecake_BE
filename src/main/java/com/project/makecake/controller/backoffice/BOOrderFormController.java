package com.project.makecake.controller.backoffice;

import com.project.makecake.service.backoffice.BOOrderFormService;
import com.project.makecake.dto.backoffice.OrderFormPeekResponseDto;
import com.project.makecake.dto.orders.OrderFormRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BOOrderFormController {
    private final BOOrderFormService boOrderFormService;

    // 주문서 양식 데이터를 잘 입력했는지 확인 API
    @PostMapping("/back-office/order-forms/peek")
    public OrderFormPeekResponseDto peekOrderForm(@RequestBody OrderFormRequestDto requestDto){
        return boOrderFormService.peekOrderForm(requestDto);
    }

    // 주문서 양식 데이터 저장 API
    @PostMapping("/back-office/order-forms/add")
    public String addOrderForm(@RequestBody OrderFormRequestDto requestDto){
        return boOrderFormService.addOrderForm(requestDto);
    }

    // 주문서 양식 삭제 API
    @DeleteMapping("/back-office/order-forms/{orderFormId}")
    public String deleteOrderForm(@PathVariable long orderFormId){
        return boOrderFormService.deleteOrderForm(orderFormId);
    }
}
