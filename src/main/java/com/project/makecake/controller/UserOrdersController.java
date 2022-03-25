package com.project.makecake.controller;

import com.project.makecake.dto.OrderFormDetailResponseDto;
import com.project.makecake.dto.UserOrderRequestDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.UserOrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class UserOrdersController {
    private final UserOrdersService userOrdersService;

    // (주문하기) 케이크 주문서 작성 API
    @PostMapping("/orders/{orderFormId}")
    public HashMap<String,Long> addUserOrders(
            @PathVariable Long orderFormId,
            @RequestBody UserOrderRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return userOrdersService.addUserOrders(orderFormId, requestDto, userDetails);
    }

}
