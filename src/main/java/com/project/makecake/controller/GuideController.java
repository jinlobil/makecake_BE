package com.project.makecake.controller;

import com.project.makecake.dto.OrderReadyStoreResponseDto;
import com.project.makecake.service.backoffice.OrderFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GuideController {
    private final OrderFormService orderFormService;

    // 주문 가능 매장 안내 페이지 조회 API
    @GetMapping("/api/pages/order-guide")
    public List<OrderReadyStoreResponseDto> getOrderReadyStoreList(){
        return orderFormService.getOrderReadyStoreList();
    }
}
