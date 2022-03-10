package com.project.makecake.repository;

import com.project.makecake.model.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {
    List<ReviewImg> findAllByReview_ReviewId(long reviewId);
    void deleteAllByReview_ReviewId(long reviewId);
}
