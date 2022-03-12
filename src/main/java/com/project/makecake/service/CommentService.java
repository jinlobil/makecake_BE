package com.project.makecake.service;

import com.project.makecake.model.Comment;
import com.project.makecake.model.Post;
import com.project.makecake.model.User;
import com.project.makecake.repository.CommentRepository;
import com.project.makecake.repository.PostRepository;
import com.project.makecake.requestDto.CommentRequestDto;
import com.project.makecake.responseDto.CommentResponseDto;
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

    // 도안 게시글의 댓글 불러오기
    public List<CommentResponseDto> getAllComments(Long postId, int page) {

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 일단 전부
        // 해당 게시글에 달린 댓글 리스트 가져오기
        Sort sort = Sort.by(Sort.Direction.DESC,"commentId");
        Pageable pageable = PageRequest.of(page,5,sort);
        Page<Comment> foundCommentList = commentRepository.findAll(pageable);

        // 반환 dto에 담기
        List<CommentResponseDto> responseDtoList = new ArrayList<>();
        for (Comment comment:foundCommentList) {
            CommentResponseDto responseDto = new CommentResponseDto(comment);
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    // 도안 댓글 저장
    @Transactional
    public void saveComment(Long postId, UserDetailsImpl userDetails, CommentRequestDto requestDto) {
        User user = userDetails.getUser();

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 댓글 저장
        Comment comment = new Comment(requestDto.getContent(),foundPost,user);
        commentRepository.save(comment);

        // 도안 게시글 댓글수 증가
        foundPost.comment(true);
    }

    // 도안 댓글 수정
    @Transactional
    public void updateComment(Long commentId, UserDetailsImpl userDetails, CommentRequestDto requestDto) {

        User user = userDetails.getUser();

        // 댓글 찾기
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 동일 유저인지 확인
        if (!user.getUserId().equals(foundComment.getUser().getUserId())) {
            throw new IllegalArgumentException("다른 사람의 댓글은 수정할 수 없습니다.");
        }

        foundComment.update(requestDto.getContent());
    }

    // 도안 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        // 댓글 찾기
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 동일 유저인지 확인
        if (!user.getUserId().equals(foundComment.getUser().getUserId())) {
            throw new IllegalArgumentException("다른 사람의 댓글은 삭제할 수 없습니다.");
        }

        // 도안 게시글 댓글수 감소
        foundComment.getPost().comment(false);

        // 연관관계 삭제
        foundComment.deleteRelation();

        // 댓글 삭제
        commentRepository.delete(foundComment);
    }
}
