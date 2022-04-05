package com.project.makecake.controller.backoffice;

import com.project.makecake.model.Cake;
import com.project.makecake.service.backoffice.AdminCakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminCakeController {

    private final AdminCakeService adminCakeService;

    // (관리자용) 가게별 케이크 사진 리스트 조회 API
    @GetMapping("/api/temp/cakes/{storeId}")
    public List<Cake> GetCakeListAtBackoffice(@PathVariable long storeId) {
        return adminCakeService.GetCakeListAtBackoffice(storeId);
    }

    // (관리자용) 케이크 사진 삭제 API
    @DeleteMapping("/api/temp/cakes/{cakeId}")
    public long deleteCake(@PathVariable long cakeId) {
        return adminCakeService.deleteCake(cakeId);
    }

    // (관리자용) 케이크 사진 저장 API
    @PostMapping("/api/temp/cakes/{storeId}")
    public void addCakeList(
            @PathVariable long storeId,
            @RequestParam List<MultipartFile> imgFileList
    ) throws IOException {
        adminCakeService.addCakeList(storeId, imgFileList);
    }
}
