package com.project.makecake.service;

import com.project.makecake.dto.*;
import com.project.makecake.enums.FolderName;
import com.project.makecake.enums.NotiType;
import com.project.makecake.exceptionhandler.CustomException;
import com.project.makecake.exceptionhandler.ErrorCode;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final DesignRepository designRepository;
    private final S3Service s3Service;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final NotiRepository notiRepository;
    private final PersonalNotiRepository personalNotiRepository;
    private final UserOrdersRepository userOrdersRepository;

    // 도안 저장 메소드
    @Transactional
    public DesignResponseDto addDesign(UserDetailsImpl userDetails, MultipartFile img) throws IOException {

        User user = userDetails.getUser();

        // S3에 이미지 업로드하고 업로드 정보 받아오기
        ImageInfoDto imgInfo = s3Service.uploadFile(img, FolderName.DESIGN.name());

        // 디비에 저장
        Design design = Design.builder()
                .imgInfo(imgInfo)
                .user(user)
                .build();
        Design savedDesign = designRepository.save(design);

        return new DesignResponseDto(savedDesign);
    }


    // 도안 삭제 메소드
    @Transactional
    public void removeDesign(UserDetailsImpl userDetails, long designId) {

        // 도안 찾기
        Design foundDesign = designRepository.findById(designId)
                .orElseThrow(()->new CustomException(ErrorCode.DESIGN_NOT_FOUND));

        // 게시되지 않은 도안인지 확인
        if (foundDesign.isPost()) {
            throw new IllegalArgumentException("게시중인 도안은 삭제할 수 없습니다.");
        }

        // 동일 유저인지 확인
        if (!foundDesign.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new CustomException(ErrorCode.NOT_DESIGN_OWNER);
        }

        userOrdersRepository.deleteByDesign(foundDesign);
        s3Service.deleteFile(foundDesign.getImgName());
        designRepository.deleteById(designId);
    }

    // 도안 게시글 리스트 조회 메소드 (54개씩)
    public List<PostSimpleResponseDto> getPostList(UserDetailsImpl userDetails, int page, String sortType) {

        // 비로그인 유저는 null 처리
        User user = null;
        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        // 15개씩 가져오기
        Page<Post> foundPostList;
        if (sortType==null || sortType.equals("createdDate")) {
            Sort sort = Sort.by(Sort.Direction.DESC,"postId");
            Pageable pageable = PageRequest.of(page,54,sort);
            foundPostList = postRepository.findAll(pageable);
        } else {
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC,sortType), new Sort.Order(Sort.Direction.DESC,"postId"));
            Pageable pageable = PageRequest.of(page,54,sort);
            foundPostList = postRepository.findAll(pageable);
        }

        // 좋아요 반영해서 반환 DTO에 담기
        List<PostSimpleResponseDto> responseDtoList = new ArrayList<>();
        for (Post post : foundPostList) {

            // myLike 디폴트는 false
            boolean myLike = false;

            // 로그인 유저는 좋아요 여부 반영
            if(user!=null) {
                Optional<PostLike> foundPostLike = postLikeRepository.findByUserAndPost(user, post);
                if (foundPostLike.isPresent()) {
                    myLike = true;
                }
            }
            PostSimpleResponseDto responseDto = PostSimpleResponseDto.builder()
                    .post(post)
                    .myLike(myLike)
                    .build();
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 도안 게시글 상세 조회 메소드
    @Transactional
    public PostDetailResponseDto getPostDetails(long postId, UserDetailsImpl userDetails) {

        // 비로그인 유저는 null 처리
        User user = null;
        if (userDetails!=null) {
            user = userDetails.getUser();
        }

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        // 조회수 추가
        foundPost.addViewCnt();

        // myLike 디폴트 false
        boolean myLike = false;

        // 로그인한 회원이면 좋아요 내역 찾아서 반영
        if(user!=null) {
            Optional<PostLike> foundPostLike = postLikeRepository.findByUserAndPost(user,foundPost);
            if (foundPostLike.isPresent()) {
                myLike = true;
            }
        }

        // 댓글 수 세기
        int commentCnt = commentRepository.countByPost(foundPost).intValue();

        return PostDetailResponseDto.builder()
                .post(foundPost)
                .myLike(myLike)
                .commentCnt(commentCnt)
                .build();
    }

    // 도안 게시글 작성 메소드
    @Transactional
    public HashMap<String,Long> addPost(long designId, UserDetailsImpl userDetails, PostRequestDto requestDto) {

        User user = userDetails.getUser();

        // 도안 찾기
        Design foundDesign = designRepository.findById(designId)
                .orElseThrow(()->new CustomException(ErrorCode.DESIGN_NOT_FOUND));

        // 이미 게시글이 작성된 도안인지 확인 (도안 하나당 게시글 하나만 작성 가능)
        if (foundDesign.isPost()) {
            throw new IllegalArgumentException("이미 게시된 도안입니다.");
        }

        // 도안 게시글 저장
        Post post = Post.builder()
                .requestDto(requestDto)
                .user(user)
                .design(foundDesign)
                .build();
        Post savedPost = postRepository.save(post);

        // 도안 post true로 변경
        foundDesign.editPostState(true);

        // 반환 객체 생성
        HashMap<String,Long> responseDto = new HashMap<>();
        responseDto.put("postId", savedPost.getPostId());
        return responseDto;
    }

    // 도안 게시글 수정 메소드
    @Transactional
    public void editPost(long postId, UserDetailsImpl userDetails, PostRequestDto requestDto) {

        User user = userDetails.getUser();

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        // 동일 유저인지 확인
        if (!foundPost.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.NOT_POST_OWNER);
        }

        // 게시글 수정
        foundPost.edit(requestDto);
        postRepository.save(foundPost);
    }

    // 도안 게시글 삭제 메소드
    @Transactional
    public void deletePost(long postId, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        // 동일 유저인지 확인
        if (!foundPost.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.NOT_POST_OWNER);
        }

        // 도안과 관계 끊고 도안 post를 false로 바꾸기
        Design connectDesign = foundPost.getDesign();
        foundPost.deleteRelation();
        connectDesign.editPostState(false);

        // 도안 게시글 좋아요 삭제
        postLikeRepository.deleteAllByPost(foundPost);

        // 도안 게시글 댓글 삭제
        commentRepository.deleteAllByPost(foundPost);

        // 게시글 삭제
        postRepository.delete(foundPost);
    }

    // 도안 게시글 좋아요 등록 및 삭제 메소드
    @Transactional
    public LikeResponseDto savePostLike(long postId, LikeRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        // 게시글 찾기
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        // 게시글 좋아요 찾기
        boolean existsPostLike = postLikeRepository.existsByUserAndPost(user,foundPost);

        // myLike가 true이면 새로운 postLike 저장
        if (requestDto.isMyLike()) {

            // 이미 좋아요를 누른 게시글이면 exception
            if (existsPostLike) {
                throw new CustomException(ErrorCode.LIKE_ALREADY_EXIST);
            }

            PostLike postLike = PostLike.builder()
                    .post(foundPost)
                    .user(user)
                    .build();
            postLikeRepository.save(postLike);

            // 좋아요 누른 유저와 게시글 작성자가 다를 경우에만 좋아요 알림 발송
            if (
                    !user.getUserId().equals(foundPost.getUser().getUserId())
                    && foundPost.getUser().getRole()!=null
            ) {
                addLikeNoti(foundPost, user);
            }

        // myLike가 false이면 기존 postLike 삭제
        } else {

            // 좋아요를 누르지 않은 게시글이면 exception
            if (!existsPostLike) {
                throw new CustomException(ErrorCode.LIKE_NOT_EXIST);
            }

            postLikeRepository.deleteByUserAndPost(user,foundPost);
        }

        // likeCnt 변경
        foundPost.addLikeCnt(requestDto.isMyLike());

        return LikeResponseDto.builder()
                .myLike(requestDto.isMyLike())
                .likeCnt(foundPost.getLikeCnt())
                .build();
    }

    // 좋아요 알림 발송 메소드
    public void addLikeNoti(Post foundPost, User createUser) {

        String redirectUrl = "https://make-cake.com/post/" + foundPost.getPostId();

        // 알림 찾기
        Noti foundNoti = notiRepository.findByType(NotiType.LIKE);

        // personalNoti 생성
        PersonalNoti personalNoti = PersonalNoti.builder()
                .recieveUser(foundPost.getUser())
                .createUser(createUser)
                .noti(foundNoti)
                .redirectUrl(redirectUrl)
                .build();

        // 저장
        personalNotiRepository.save(personalNoti);
    }
}
