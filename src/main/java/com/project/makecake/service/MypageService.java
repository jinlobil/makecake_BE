package com.project.makecake.service;

import com.project.makecake.dto.*;
import com.project.makecake.model.*;
import com.project.makecake.repository.*;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        String provider = "makecake";
        if (findUser.getProvider() != null){
            provider = findUser.getProvider();
        }
        String email = findUser.getUsername();
        if (findUser.getProviderEmail() != null){
            email = findUser.getProviderEmail();
        }
        MypageResponseDto mypage = MypageResponseDto.builder()
                .nickname(findUser.getNickname())
                .userPicture(findUser.getProfileImgUrl())
                .provider(provider)
                .email(email)
                .build();
        return mypage;
    }

    // 내가 그린 도안 조회
    public List<MyDesignResponseDto> myDesigns(String option, UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        List<MyDesignResponseDto> designList = new ArrayList<>();
        if (option.equals("nonpost")){
            List<Design> findDesign = designRepository.findByState(DesignState.UNPOST);
            for (Design design : findDesign){
                MyDesignResponseDto responseDto = MyDesignResponseDto.builder()
                        .designId(design.getDesignId())
                        .img(design.getImgUrl())
                        .build();
                designList.add(responseDto);
            }
        } else if (option.equals("post")){
            List<Post> findPost = postRepository.findByUser(findUser);
            for (Post post : findPost){
                MyDesignResponseDto responseDto = MyDesignResponseDto.builder()
                        .postId(post.getPostId())
                        .img(post.getDesign().getImgUrl())
                        .build();
                designList.add(responseDto);
            }
        }
        return designList;
    }

    // 내가 좋아요 한 게시글
    public List<MyReactDesignResponceDto> myReactDesigns(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        List<PostLike> findPost = postLikeRepository.findByUser(findUser);
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
    public List<MyCommentResponseDto> myComments(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        List<Comment> findComment = commentRepository.findByUser(findUser);
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
    public List<MyReactStoreResponseDto> myReactStores(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        List<StoreLike> findStore = storeLikeRepository.findByUser(findUser);
        List<MyReactStoreResponseDto> storeList = new ArrayList<>();
        for (StoreLike storeLike : findStore){
            String address = storeLike.getStore().getFullAddress();
            String[] custom = address.split(" ");
            String addressSimple = custom[0] + custom[1] + custom[2];
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
    public List<MyReviewResponseDto> myReviews(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        List<Review> findReview = reviewRepository.findByUser(findUser);
        List<MyReviewResponseDto> reviewList = new ArrayList<>();
        for (Review review : findReview){
            List<ReviewImg> reviewImgs = reviewImgRepository.findAllByReview_ReviewId(review.getReviewId());
            MyReviewResponseDto responseDto = MyReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .storeId(review.getStore().getStoreId())
                    .name(review.getStore().getName())
                    .content(review.getContent())
                    .createdDate(review.getCreatedAt())
                    .mainImg(reviewImgs.get(0).getImgUrl())
                    .writerNickname(review.getUser().getNickname())
                    .build();
            reviewList.add(responseDto);
        }
        return reviewList;
    }

    // 내가 좋아요 한 케이크
    public List<MyReactCakeResponseDto> myReactCakes(UserDetailsImpl userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        List<CakeLike> findCake = cakeLikeRepository.findByUser(findUser);
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
