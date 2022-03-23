package com.project.makecake.service;

import com.project.makecake.model.Comment;
import com.project.makecake.model.Post;
import com.project.makecake.model.User;
import com.project.makecake.repository.CommentRepository;
import com.project.makecake.repository.PostRepository;
import com.project.makecake.dto.CommentRequestDto;
import com.project.makecake.dto.CommentResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 도안 댓글 리스트 조회 메소드 (5개씩)
    public List<CommentResponseDto> getCommentList(long postId, int page) {

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 게시글에 달린 댓글 리스트 찾기 (5개씩)
        Sort sort = Sort.by(Sort.Direction.DESC,"commentId");
        Pageable pageable = PageRequest.of(page,5,sort);
        Page<Comment> foundCommentList = commentRepository.findAllByPost(foundPost, pageable);

        // 반환 DTO에 담기
        List<CommentResponseDto> responseDtoList = new ArrayList<>();
        for (Comment comment : foundCommentList) {
            CommentResponseDto responseDto = new CommentResponseDto(comment);
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    // 도안 댓글 작성 메소드
    @Transactional
    public void addComment(long postId, UserDetailsImpl userDetails, CommentRequestDto requestDto) {

        User user = userDetails.getUser();

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 댓글 저장
        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .post(foundPost)
                .user(user)
                .build();
        commentRepository.save(comment);

        // 도안 게시글 댓글수 증가
        foundPost.editCommentCnt(true);
    }

    // 도안 댓글 수정 메소드
    @Transactional
    public void editComment(long commentId, UserDetailsImpl userDetails, CommentRequestDto requestDto) {

        User user = userDetails.getUser();

        // 댓글 찾기
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 동일 유저인지 확인
        if (!user.getUserId().equals(foundComment.getUser().getUserId())) {
            throw new IllegalArgumentException("다른 사람의 댓글은 수정할 수 없습니다.");
        }

        foundComment.editContent(requestDto.getContent());
    }

    // 도안 댓글 삭제 메소드
    @Transactional
    public void deleteComment(long commentId, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        // 댓글 찾기
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 동일 유저인지 확인
        if (!user.getUserId().equals(foundComment.getUser().getUserId())) {
            throw new IllegalArgumentException("다른 사람의 댓글은 삭제할 수 없습니다.");
        }

        // 도안 게시글 댓글수 감소
        foundComment.getPost().editCommentCnt(false);

        // 연관관계 삭제
        foundComment.deleteRelation();

        // 댓글 삭제
        commentRepository.delete(foundComment);
    }
}
