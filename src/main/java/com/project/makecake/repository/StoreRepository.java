package com.project.makecake.repository;

import com.project.makecake.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findTop5ByOrderByLikeCntDesc();
    List<Store> findAllByNameStartingWithOrderByLikeCntDesc(String searchText);
    List<Store> findAllByNameStartingWithOrderByReviewCntDesc(String searchText);
    List<Store> findByFullAddressContainingOrderByLikeCntDesc(String searchText);
    List<Store> findByFullAddressContainingOrderByReviewCntDesc(String searchText);
    List<Store> findByXBetweenAndYBetweenOrderByLikeCntDesc(float minX, float maxX, float minY, float maxY);
    List<Store> findByXBetweenAndYBetweenOrderByReviewCntDesc(float minX, float maxX, float minY, float maxY);

    List<Store> findByNameStartingWith(String searchText);

}
