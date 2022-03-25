package com.project.makecake.controller;

import com.project.makecake.dto.UserOrderRequestDto;
import com.project.makecake.dto.UserOrdersDetailResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.UserOrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    // (주문하기) 사용자가 작성한 주문서 상세 보기
    @GetMapping("/orders/{userOrdersId}")
    public UserOrdersDetailResponseDto getUserOrdersDetails(
            @PathVariable long userOrdersId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return userOrdersService.getUserOrdersDetails(userOrdersId, userDetails);
    }

    // 주문서의 도안 전송 API
    @GetMapping("/orders/{userOrdersId}/design")
    public ResponseEntity<byte[]> getDesignAtOrders(@PathVariable long userOrdersId) throws IOException {
        return userOrdersService.getDesignAtOrders(userOrdersId);
    }

}
