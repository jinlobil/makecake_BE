package com.project.makecake.service;

import com.project.makecake.dto.HomeReviewDto;
import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.dto.StoreDetailResponseDto;
import com.project.makecake.model.*;
import com.project.makecake.repository.ReviewImgRepository;
import com.project.makecake.repository.ReviewRepository;
import com.project.makecake.repository.StoreRepository;
import com.project.makecake.requestDto.ReviewRequestDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
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
    public List<HomeReviewDto> getHomeReviewList(int page, int size) {
        //페이징
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Review> rawReviewList = reviewRepository.findAllByOrderByCreatedAtDesc(pageable);

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
            String img = "";
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
    public void writeReview(long storeId,
                            ReviewRequestDto requestDto,
                            List<MultipartFile> imgFiles,
                            UserDetailsImpl userDetails) throws IOException {
        User user = userDetails.getUser();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 매장입니다."));

        //본문은 무조건 있어야 함
        Review review = new Review(requestDto.getContent(), store, user);
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
    public void updateReview(long reviewId, ReviewRequestDto requestDto, List<MultipartFile> imgFiles, UserDetailsImpl userDetails){
        //requestDto, imgFiles 중 null이 아닌 것
        //내용만 수정한 경우 : List<MultipartFile> == null
        //사진만 수정한 경우 : content == null, List<MultipartFile> != null
        //사진 중 일부만 수정한 경우 => 싹 다 지우고 다 덮어쓰기?

    }
}
