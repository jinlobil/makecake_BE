package com.project.makecake.controller.backoffice;

import com.project.makecake.dto.backoffice.BoSearchStoreIdRequestDto;
import com.project.makecake.dto.backoffice.BoSearchStoreIdResponseDto;
import com.project.makecake.dto.backoffice.CakeMenuOptionPeekRequestDto;
import com.project.makecake.dto.backoffice.CakeMenuOptionPeekResponseDto;
import com.project.makecake.service.backoffice.BackOfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BackOfficeController {
    private final BackOfficeService backOfficeService;

    @PostMapping("/api/backOffice/store/peekMenuAndOption")
    public CakeMenuOptionPeekResponseDto peekMenuAndOption(@RequestBody CakeMenuOptionPeekRequestDto requestDto){
        return backOfficeService.peekMenuAndOption(requestDto);
    }

    @PostMapping("/api/backOffice/store/saveMenuAndOption")
    public String addMenuAndOption(@RequestBody CakeMenuOptionPeekRequestDto requestDto){
        return backOfficeService.addMenuAndOption(backOfficeService.peekMenuAndOption(requestDto));
    }

    @PostMapping("/api/backOffice/store/searchId")
    public BoSearchStoreIdResponseDto boSearchStoreId(@RequestBody BoSearchStoreIdRequestDto requestDto){
        return backOfficeService.boSearchStoreId(requestDto);
    }

    /*
    @GetMapping("/api/backOffice/cake/thumbnail-img")
    public void addCakeThumbNailImg(
            @RequestParam int page
    ) throws IOException {
        backOfficeService.addCakeThumbNailImg(page);
    }
     */

}
