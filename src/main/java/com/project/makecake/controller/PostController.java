package com.project.makecake.controller;

import com.project.makecake.responseDto.DesignResponseDto;
import com.project.makecake.responseDto.PostSimpleResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    // 도안 그리고 저장 API
    @PostMapping("/designs")
    public DesignResponseDto saveDesign(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam MultipartFile img) throws IOException {
        return postService.saveDesign(userDetails,img);
    }

    // 정렬타입 적용 안함
    // 게시된 도안 사진 리스트 API
    @GetMapping("/api/designs")
    public List<PostSimpleResponseDto> getAllPosts() {
        return postService.getAllPosts();
    }


}
