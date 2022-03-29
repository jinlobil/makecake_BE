package com.project.makecake.repository;

import com.project.makecake.model.Review;
import com.project.makecake.model.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {
    List<ReviewImg> findAllByReview_ReviewId(long reviewId);
    ReviewImg findTop1ByReview(Review review);
    ReviewImg findByReview(Review review);
    void deleteAllByReview_ReviewId(long reviewId);
    void deleteByImgUrl(String imgUrl);
    ReviewImg findByImgUrl(String imgUrl);


}
