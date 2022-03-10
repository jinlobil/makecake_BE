package com.project.makecake.repository;

import com.project.makecake.model.Review;
import com.project.makecake.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Review> findTop3ByStoreOrderByCreatedAtDesc(Store store);
}
