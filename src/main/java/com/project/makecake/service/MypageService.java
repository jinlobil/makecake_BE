package com.project.makecake.service;

import com.project.makecake.dto.MypageResponseDto;
import com.project.makecake.model.User;
import com.project.makecake.repository.*;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final UserRepository userRepository;
//    private final DesignRepository designRepository;
//    private final CakeLikeRepository cakeLikeRepository;
//    private final CommentRepository commentRepository;
//    private final StoreLikeRepository storeLikeRepository;
//    private final ReviewRepository reviewRepository;

    // 마이페이지 조회
    public MypageResponseDto mypage(UserDetailsImpl userDetails) {
        Optional<User> findUser = userRepository.findByUsername(userDetails.getUsername());
        MypageResponseDto mypage = new MypageResponseDto();
        mypage.setNickname(findUser.get().getNickname());

        return mypage;
    }

    // 내가 그린 도안 조회
    public void myDesigns(String option, UserDetailsImpl userDetails) {
    }

    // 내가 좋아요 한 게시글
    public void myReactDesigns(UserDetailsImpl userDetails) {
    }

    // 내가 남긴 댓글
    public void myComments(UserDetailsImpl userDetails) {
    }

    // 내가 좋아요 한 매장
    public void myReactStores(UserDetailsImpl userDetails) {
    }

    // 내가 남긴 후기
    public void myReviews(UserDetailsImpl userDetails) {
    }

    // 내가 좋아요 한 케이크
    public void myReactCakes(UserDetailsImpl userDetails) {
    }

}
