package com.project.makecake.service;

import com.project.makecake.dto.*;
import com.project.makecake.enums.DesignState;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import com.project.makecake.responseDto.DesignResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final UserRepository userRepository;
    private final DesignRepository designRepository;
    private final CakeLikeRepository cakeLikeRepository;
    private final CommentRepository commentRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final PostLikeRepository postLikeRepository;

    // 마이페이지 조회
    public MypageResponseDto mypage(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        String email = findUser.getUsername();
        if (findUser.getProviderEmail() != null){
            email = findUser.getProviderEmail();
        }
        MypageResponseDto mypage = MypageResponseDto.builder()
                .nickname(findUser.getNickname())
                .userPicture(findUser.getProfileImgUrl())
                .email(email)
                .build();
        return mypage;
    }

    // 내가 그린 도안 조회
    public List<MyDesignResponseDto> myDesigns(UserDetailsImpl userDetails, String option, int page) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Pageable pageable = PageRequest.of(page, 18);
        List<MyDesignResponseDto> designList = new ArrayList<>();
        if (option.equals("nonpost")){
            Page<Design> findDesign = designRepository.findByUserAndState(findUser, DesignState.UNPOST, pageable);
            for (Design design : findDesign){
                MyDesignResponseDto responseDto = MyDesignResponseDto.builder()
                        .designId(design.getDesignId())
                        .img(design.getImgUrl())
                        .build();
                designList.add(responseDto);
            }
        } else if (option.equals("post")){
            Page<Post> findPost = postRepository.findByUser(findUser, pageable);
            for (Post post : findPost){
                MyDesignResponseDto responseDto = MyDesignResponseDto.builder()
                        .postId(post.getPostId())
                        .designId(post.getDesign().getDesignId())
                        .img(post.getDesign().getImgUrl())
                        .build();
                designList.add(responseDto);
            }
        }
        return designList;
    }

    // 내가 그린 도안 상세 조회(게시X)
    public DesignResponseDto getDesign(Long designId, UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }

        // 도안 찾기
        Design foundDesign = designRepository.findById(designId)
                .orElseThrow(()->new IllegalArgumentException("도안이 존재하지 않습니다."));

        DesignResponseDto responseDto = new DesignResponseDto(foundDesign);

        return responseDto;
    }

    // 내가 좋아요 한 게시글
    public List<MyReactDesignResponceDto> myReactDesigns(UserDetailsImpl userDetails, int page) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Pageable pageable = PageRequest.of(page, 5);
        Page<PostLike> findPost = postLikeRepository.findByUser(findUser, pageable);
        List<MyReactDesignResponceDto> reactList = new ArrayList<>();
        for (PostLike postLike : findPost){
            MyReactDesignResponceDto responceDto = MyReactDesignResponceDto.builder()
                    .postId(postLike.getPost().getPostId())
                    .img(postLike.getPost().getDesign().getImgUrl())
                    .build();
            reactList.add(responceDto);
        }
        return reactList;
    }

    // 내가 남긴 댓글
    public List<MyCommentResponseDto> myComments(UserDetailsImpl userDetails, int page) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Pageable pageable = PageRequest.of(page, 5);
        Page<Comment> findComment = commentRepository.findByUser(findUser, pageable);
        List<MyCommentResponseDto> commentList = new ArrayList<>();
        for (Comment comment : findComment){
            MyCommentResponseDto responseDto = MyCommentResponseDto.builder()
                    .commentId(comment.getCommentId())
                    .content(comment.getContent())
                    .createdDate(comment.getCreatedAt())
                    .postId(comment.getPost().getPostId())
                    .postTitle(comment.getPost().getTitle())
                    .writerNickname(comment.getUser().getNickname())
                    .build();
            commentList.add(responseDto);
        }
        return commentList;
    }

    // 내가 좋아요 한 매장
    public List<MyReactStoreResponseDto> myReactStores(UserDetailsImpl userDetails, int page) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Pageable pageable = PageRequest.of(page, 8);
        Page<StoreLike> findStore = storeLikeRepository.findByUser(findUser, pageable);
        List<MyReactStoreResponseDto> storeList = new ArrayList<>();
        for (StoreLike storeLike : findStore){
            String address = storeLike.getStore().getFullAddress();
            String[] custom = address.split(" ");
            String addressSimple = custom[0] + " " + custom[1] + " " + custom[2];
            MyReactStoreResponseDto responseDto = MyReactStoreResponseDto.builder()
                    .storeId(storeLike.getStore().getStoreId())
                    .name(storeLike.getStore().getName())
                    .addressSimple(addressSimple)
                    .mainImg(storeLike.getStore().getMainImg())
                    .build();
            storeList.add(responseDto);
        }
        return storeList;
    }

    // 내가 남긴 후기
    public List<MyReviewResponseDto> myReviews(UserDetailsImpl userDetails, int page) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Pageable pageable = PageRequest.of(page, 5);
        Page<Review> findReview = reviewRepository.findByUser(findUser, pageable);
        List<MyReviewResponseDto> reviewList = new ArrayList<>();
        for (Review review : findReview){
            ReviewImg reviewImg = reviewImgRepository.findTop1ByReview(review);
            String reviewImgUrl = "https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png";
            if (reviewImg != null) {
                reviewImgUrl = reviewImg.getImgUrl();
            }
            MyReviewResponseDto responseDto = MyReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .storeId(review.getStore().getStoreId())
                    .name(review.getStore().getName())
                    .content(review.getContent())
                    .createdDate(review.getCreatedAt())
                    .mainImg(reviewImgUrl)
                    .writerNickname(review.getUser().getNickname())
                    .build();
            reviewList.add(responseDto);
        }
        return reviewList;
    }

    // 내가 좋아요 한 케이크
    public List<MyReactCakeResponseDto> myReactCakes(UserDetailsImpl userDetails, int page) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인을 해주세요.");
        }
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Pageable pageable = PageRequest.of(page, 8);
        Page<CakeLike> findCake = cakeLikeRepository.findByUser(findUser, pageable);
        List<MyReactCakeResponseDto> cakeList = new ArrayList<>();
        for (CakeLike cakeLike : findCake){
            MyReactCakeResponseDto responseDto = MyReactCakeResponseDto.builder()
                    .cakeId(cakeLike.getCake().getCakeId())
                    .img(cakeLike.getCake().getUrl())
                    .storeName(cakeLike.getCake().getStore().getName())
                    .likeCnt(cakeLike.getCake().getLikeCnt())
                    .myLike(true)
                    .build();
            cakeList.add(responseDto);
        }
        return cakeList;
    }

}
