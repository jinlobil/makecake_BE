package com.project.makecake.controller.backoffice;

import com.project.makecake.dto.UserInfoDto;
import com.project.makecake.service.backoffice.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/role/{userId}")
    public void editRole(@PathVariable Long userId, @RequestBody UserInfoDto dto) {
        adminService.editRole(userId, dto);
    }

    // 전체 유저 불러오기
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/getUsers")
    public List<UserInfoDto> getUsers() {
        return adminService.getUsers();
    }

    // 케이크 사진 삭제
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/deleteCake/{cakeId}")
    public void deleteCake(@PathVariable Long cakeId){
        adminService.deleteCake(cakeId);
    }
}
