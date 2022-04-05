package com.project.makecake.controller.backoffice;

import com.project.makecake.dto.backoffice.*;
import com.project.makecake.service.backoffice.BOMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BOMenuController {
    private final BOMenuService BOMenuService;

    // 케이크 메뉴와 옵션 데이터를 잘 입력했는지 확인 API
    @PostMapping("/back-office/menus/peek")
    public MenuAndOptionResponseDto peekMenuAndOption(@RequestBody MenuAndOptionRequestDto requestDto){
        return BOMenuService.peekMenuAndOption(requestDto);
    }

    // 케이크 메뉴와 옵션 데이터 저장 API
    @PostMapping("/back-office/menus/add")
    public String addMenuAndOption(@RequestBody MenuAndOptionRequestDto requestDto){
        return BOMenuService.addMenuAndOption(BOMenuService.peekMenuAndOption(requestDto));
    }

    // 케이크 매장명 검색 시 storeId 반환 API
    @PostMapping("/back-office/stores/find-store-id")
    public FindStoreIdResponseDto findStoreId(@RequestBody FindStoreIdRequestDto requestDto){
        return BOMenuService.findStoreId(requestDto);
    }

    // 케이크 메뉴 삭제 메소드
    @DeleteMapping ("back-office/menus/cake-menus")
    public void deleteCakeMenu(long storeId){
        BOMenuService.deleteCakeMenu(storeId);
    }

    // 케이크 옵션 삭제 메소드
    @DeleteMapping("back-office/menus/cake-options")
    public void deleteCakeOption(long storeId){
        BOMenuService.deleteCakeOption(storeId);
    }


}
