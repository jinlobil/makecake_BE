package com.project.makecake.backOffice.controller;

import com.project.makecake.backOffice.dto.BoSearchStoreIdRequestDto;
import com.project.makecake.backOffice.dto.BoSearchStoreIdResponseDto;
import com.project.makecake.backOffice.dto.CakeMenuPeekRequestDto;
import com.project.makecake.backOffice.dto.CakeMenuPeekResponseDto;
import com.project.makecake.backOffice.service.BackOfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BackOfficeController {
    private final BackOfficeService backOfficeService;

    @PostMapping("/api/backOffice/store/peekMenuAndOption")
    public CakeMenuPeekResponseDto peekMenuAndOption(@RequestBody CakeMenuPeekRequestDto requestDto){
        return backOfficeService.peekMenuAndOption(requestDto);
    }

    @PostMapping("/api/backOffice/store/saveMenuAndOption")
    public String saveMenuAndOption(@RequestBody CakeMenuPeekRequestDto requestDto){
        return backOfficeService.saveMenuAndOption(backOfficeService.peekMenuAndOption(requestDto));
    }

    @PostMapping("/api/backOffice/store/searchId")
    public BoSearchStoreIdResponseDto boSearchStoreId(@RequestBody BoSearchStoreIdRequestDto requestDto){
        return backOfficeService.boSearchStoreId(requestDto);
    }
}
