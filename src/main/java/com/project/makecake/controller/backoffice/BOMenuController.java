package com.project.makecake.controller.backoffice;

import com.project.makecake.dto.backoffice.*;
import com.project.makecake.dto.orders.OrderFormRequestDto;
import com.project.makecake.service.backoffice.BackOfficeService;
import com.project.makecake.service.backoffice.OrderFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BOMenuController {
    private final BackOfficeService backOfficeService;

    // 케이크 메뉴와 옵션 데이터를 잘 입력했는지 확인 API
    @PostMapping("/back-office/menus/peek")
    public CakeMenuOptionPeekResponseDto peekMenuAndOption(@RequestBody CakeMenuOptionPeekRequestDto requestDto){
        return backOfficeService.peekMenuAndOption(requestDto);
    }

    // 케이크 메뉴와 옵션 데이터 저장 API
    @PostMapping("/back-office/menus/add")
    public String addMenuAndOption(@RequestBody CakeMenuOptionPeekRequestDto requestDto){
        return backOfficeService.addMenuAndOption(backOfficeService.peekMenuAndOption(requestDto));
    }

    // 케이크 매장명 검색 시 storeId 반환 API
    @PostMapping("/back-office/stores/find-store-id")
    public findStoreIdResponseDto findStoreId(@RequestBody findStoreId requestDto){
        return backOfficeService.findStoreId(requestDto);
    }

}
