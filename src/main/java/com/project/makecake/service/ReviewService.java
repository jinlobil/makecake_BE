package com.project.makecake.service;

import com.project.makecake.dto.HomeReviewDto;
import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.enums.FolderName;
import com.project.makecake.model.*;
import com.project.makecake.repository.ReviewImgRepository;
import com.project.makecake.repository.ReviewRepository;
import com.project.makecake.repository.StoreRepository;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final S3UploadService s3UploadService;
    private final StoreRepository storeRepository;

    //홈탭 : 최신 리뷰 보여주기 (페이지네이션)
    @Transactional
    public List<HomeReviewDto> getHomeReviewList() {
        //페이징
//        Pageable pageable = PageRequest.of(page-1, size);
//        Page<Review> rawReviewList = reviewRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<Review> rawReviewList = reviewRepository.findTop5ByOrderByCreatedAtDesc();

        List<HomeReviewDto> responseDtoList = new ArrayList<>();
        for(Review rawReview : rawReviewList){
            Store store = rawReview.getStore();
            HomeReviewDto responseDto = new HomeReviewDto();
            long reviewId = rawReview.getReviewId();
            responseDto.setReviewId(reviewId);
            responseDto.setNickname(rawReview.getUser().getNickname());
            responseDto.setCreatedDate(rawReview.getCreatedAt());
            responseDto.setContent(rawReview.getContent());
            responseDto.setStoreId(store.getStoreId());
            responseDto.setStoreName(store.getName());
            String img = "https://makecake.s3.ap-northeast-2.amazonaws.com/PROFILE/18d2090b-1b98-4c34-b92b-a9f50d03bd53makecake_default.png";
            if(!reviewImgRepository.findAllByReview_ReviewId(reviewId).isEmpty()){
                img = reviewImgRepository.findAllByReview_ReviewId(reviewId).get(0).getImgUrl();
            }
            responseDto.setImg(img);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    //리뷰 작성하기
    @Transactional
    public void writeReview(long storeId, String content, List<MultipartFile> imgFiles, UserDetailsImpl userDetails) throws IOException {
        User user = userDetails.getUser();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 매장입니다."));

        //본문은 무조건 있어야 함
        Review review = new Review(content, store, user);
        reviewRepository.save(review);

        //이미지도 업로드할 경우 저장해줌
        if(imgFiles != null){
            for(MultipartFile imgFile : imgFiles){
                ImageInfoDto imageInfoDto = s3UploadService.uploadFile(imgFile, FolderName.REVIEW.name());
                ReviewImg reviewImg = new ReviewImg(imageInfoDto, review);
                reviewImgRepository.save(reviewImg);
            }
        }
        store.setReviewCnt(store.getReviewCnt() +1);
        storeRepository.save(store);
    }

    //리뷰 삭제하기
    @Transactional
    public void deleteReview(long reviewId){
        Store store = reviewRepository.getById(reviewId).getStore();

        reviewImgRepository.deleteAllByReview_ReviewId(reviewId);
        reviewRepository.deleteById(reviewId);

        store.setReviewCnt(store.getReviewCnt() -1);
        storeRepository.save(store);
    }


    //리뷰 수정하기 (프론트와 상의 후 구현 필요함)
    @Transactional
    public void updateReview(long reviewId, String content, List<MultipartFile> imgFiles, List<String> imgUrls, UserDetailsImpl userDetails) throws IOException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        if(!review.getUser().equals(userDetails.getUser())){
            throw new IllegalArgumentException("다른 회원이 작성한 리뷰는 수정할 수 없습니다.");
        }

        //imgFiles 들어온 경우
        if(imgFiles != null){
            for(MultipartFile imgFile : imgFiles){
                ImageInfoDto imageInfoDto = s3UploadService.uploadFile(imgFile, FolderName.REVIEW.name());
                ReviewImg reviewImg = new ReviewImg(imageInfoDto, review);
                reviewImgRepository.save(reviewImg);
            }
        }

        //content : 다시 업데이트
        review.setContent(content);
        reviewRepository.save(review);

        //imgUrls (기존에 있던 이미지 URL 중 사용자가 삭제한 것만 삭제)
        if(reviewImgRepository.findAllByReview_ReviewId(reviewId) != null){
            List<ReviewImg> originReviewImgList = reviewImgRepository.findAllByReview_ReviewId(reviewId);
            if(originReviewImgList.size() != imgUrls.size()){
                List<String> originUrlList = new ArrayList<>();
                //s3에서 지우려면?
                for(int i=0; i< originReviewImgList.size(); i++){
                    originUrlList.add(originReviewImgList.get(i).getImgUrl());
                }
                originUrlList.removeAll(imgUrls);

                for(int j=0; j< originUrlList.size(); j++){
                    reviewImgRepository.deleteByImgUrl(originUrlList.get(j));
                }
            }
        }
    }
}
