package com.project.makecake.controller;

import com.project.makecake.requestDto.CommentRequestDto;
import com.project.makecake.responseDto.CommentResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import com.project.makecake.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    // 일단 전부
    // 도안 댓글 리스트 불러오기 API
    @GetMapping("/api/designs/{postId}/comments")
    public List<CommentResponseDto> getAllComments(@PathVariable Long postId) {
        return commentService.getAllComments(postId);
    }

    // 도안 댓글 작성 API
    @PostMapping("/comments/{postId}")
    public void saveComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentRequestDto requestDto
            ) {
        commentService.saveComment(postId,userDetails,requestDto);
    }

    // 도안 댓글 수정 API
    @PutMapping("/comments/{commentId}")
    public void updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentRequestDto requestDto
    ) {
        commentService.updateComment(commentId,userDetails,requestDto);
    }

    // 도안 댓글 삭제 API
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        commentService.deleteComment(commentId,userDetails);
    }
}
