package com.project.makecake.service;

import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import com.project.makecake.requestDto.PostRequestDto;
import com.project.makecake.responseDto.DesignResponseDto;
import com.project.makecake.responseDto.LikeResponseDto;
import com.project.makecake.responseDto.PostDetailResponseDto;
import com.project.makecake.responseDto.PostSimpleResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final DesignRepository designRepository;
    private final S3UploadService s3UploadService;
    private final StoreRepository storeRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    // 게시된 도안 사진 리스트
    public List<PostSimpleResponseDto> getAllPosts(UserDetailsImpl userDetails) {

        User user = null;

        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        List<Post> foundPostList = postRepository.findAll();

        List<PostSimpleResponseDto> responseDtoList = new ArrayList<>();

        for (Post post : foundPostList) {
            boolean myLike = false;
            if(user!=null) {
                Optional<PostLike> foundPostLike = postLikeRepository.findByUserAndPost(user,post);
                if (foundPostLike.isPresent()) {
                    myLike = true;
                }
            }
            PostSimpleResponseDto responseDto = new PostSimpleResponseDto(post);
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    // 도안 저장
    @Transactional
    public DesignResponseDto saveDesign(UserDetailsImpl userDetails, MultipartFile img) throws IOException {
        User user = userDetails.getUser();
        ImageInfoDto imgInfo = s3UploadService.uploadFile(img, FolderName.DESIGN.name());
        Design design = new Design(imgInfo,user);
        Design savedDesign = designRepository.save(design);
        return new DesignResponseDto(savedDesign);
    }

    // 도안 게시
    @Transactional
    public void savePost(Long designId, UserDetailsImpl userDetails, PostRequestDto requestDto) {
        User user = userDetails.getUser();
        Design foundDesign = designRepository.findById(designId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 도안입니다."));

        if (foundDesign.getState().equals(DesignState.POST)) {
            throw new IllegalArgumentException("이미 게시된 도안입니다.");
        }

        if (requestDto.isMade()&&requestDto.getStoreId()!=null) {
            Store foundStore = storeRepository.findById(requestDto.getStoreId())
                    .orElseThrow(()->new IllegalArgumentException("존재하지 않는 매장입니다."));
            Post post = new Post(requestDto,user,foundDesign,foundStore);
            postRepository.save(post);
            foundDesign.post();
        } else {
            Post post = new Post(requestDto,user,foundDesign);
            postRepository.save(post);
            foundDesign.post();
        }

    }

    // 도안 게시글 수정
    @Transactional
    public void updatePost(Long postId, UserDetailsImpl userDetails, PostRequestDto requestDto) {
        User user = userDetails.getUser();
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!foundPost.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("다른 사람이 쓴 게시글은 수정할 수 없습니다.");
        }

        if (requestDto.isMade()&&requestDto.getStoreId()!=null) {
            Store foundStore = storeRepository.findById(requestDto.getStoreId())
                    .orElseThrow(()->new IllegalArgumentException("존재하지 않는 매장입니다."));
            foundPost.update(requestDto,foundStore);
            postRepository.save(foundPost);
        } else {
            foundPost.update(requestDto);
            postRepository.save(foundPost);
        }
    }

    // 도안 게시글 삭제
    @Transactional
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!foundPost.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("다른 사람의 게시글은 삭제할 수 없습니다.");
        }

        // 디자인 UNPOST로 바꾸기
        Design connectDesign = foundPost.getDesign();
        foundPost.deleteRelation();
        connectDesign.unpost();

        // 도안 게시글 좋아요 삭제
        postLikeRepository.deleteAllByPost(foundPost);

        // 도안 게시글 댓글 삭제
        commentRepository.deleteAllByPost(foundPost);

        postRepository.delete(foundPost);
    }

    // 도안 게시글 좋아요
    @Transactional
    public LikeResponseDto postLike(Long postId, boolean myLike, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // myLike가 true이면 새로운 postLike 저장
        if (myLike) {
            PostLike postLike = new PostLike(foundPost, user);
            postLikeRepository.save(postLike);
            // myLike가 false이면 기존 postLike 삭제
        } else {
            postLikeRepository.deleteByUserAndPost(user,foundPost);
        }
        // likeCnt 변경
        boolean likeResult = foundPost.likePost(myLike);
        return new LikeResponseDto(likeResult);
    }

    // 도안 게시글 상세
    @Transactional
    public PostDetailResponseDto getPost(Long postId, UserDetailsImpl userDetails) {

        User user = null;

        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 조회수 추가
        foundPost.viewPost();

        boolean myLike = false;

        // 로그인한 회원이면 좋아요 내역 찾아서 반영
        if(user!=null) {
            Optional<PostLike> foundPostLike = postLikeRepository.findByUserAndPost(user,foundPost);
            if (foundPostLike.isPresent()) {
                myLike = true;
            }
        }

        return new PostDetailResponseDto(foundPost,myLike);

    }
}
