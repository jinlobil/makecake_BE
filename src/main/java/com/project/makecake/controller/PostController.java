package com.project.makecake.controller;

import com.project.makecake.dto.LikeDto;
import com.project.makecake.dto.PostRequestDto;
import com.project.makecake.dto.DesignResponseDto;
import com.project.makecake.dto.PostDetailResponseDto;
import com.project.makecake.dto.PostSimpleResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    // 도안 그리고 난 후 이미지 저장하는 API
    @PostMapping("/designs")
    public DesignResponseDto addDesign(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam MultipartFile imgFile) throws IOException {
        return postService.addDesign(userDetails, imgFile);
    }

    // 도안 삭제 API
    @DeleteMapping("/designs/{designId}")
    public void removeDesign(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long designId
    ) {
        postService.removeDesign(userDetails, designId);
    }

    // 게시된 도안 사진 리스트 API (18개씩)
    @GetMapping("/api/designs")
    public List<PostSimpleResponseDto> getPostList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int page,
            @RequestParam(required = false) String sortType
    ) {
        return postService.getPostList(userDetails, page, sortType);
    }

    // 게시된 도안 상세 API
    @GetMapping("/api/designs/{postId}")
    public PostDetailResponseDto getPostDetails(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return postService.getPostDetails(postId, userDetails);
    }

    // 도안 게시글 작성 API
    @PostMapping("/posts/{designId}")
    public HashMap<String,Long> addPost(
            @PathVariable Long designId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PostRequestDto requestDto
    ) {
        return postService.addPost(designId, userDetails, requestDto);
    }

    // 도안 게시글 수정 API
    @PutMapping("/posts/{postId}")
    public void editPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PostRequestDto requestDto
    ) {
        postService.editPost(postId, userDetails, requestDto);
    }

    // 도안 게시글 삭제 API
    @DeleteMapping("/posts/{postId}")
    public void deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        postService.deletePost(postId, userDetails);
    }

    // 도안 게시글 좋아요 API
    @PostMapping("/posts/like/{postId}")
    public LikeDto likePost(
            @PathVariable Long postId,
            @RequestBody LikeDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return postService.likePost(postId, requestDto, userDetails);
    }
}
