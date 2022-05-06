package com.project.makecake.controller.backoffice;

import com.project.makecake.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class BOOpenApiController {
    private final OpenApiService openApiService;

    // (초기 데이터 쌓기) naver 지도 api 요청으로 레터링 케이크 매장 정보 저장 메소드
    @Secured("ROLE_ADMIN")
    @PostMapping("/back-office/open-api/stores")
    public void collectStoreData(@RequestParam int storeNo) throws IOException {
        openApiService.collectStoreData(storeNo);
    }

}
