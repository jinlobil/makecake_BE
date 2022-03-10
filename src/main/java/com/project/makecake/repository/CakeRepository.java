package com.project.makecake.repository;

import com.project.makecake.model.Cake;
import com.project.makecake.model.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CakeRepository extends JpaRepository<Cake, Long> {
    List<Cake> findAllByStore(Store store);

    // 좋아요순->최신순으로 정렬, 좋아요 수가 변경되는 데이터가 있으면 중복 발생
//    @Query(value = "select c from Cake as c where (c.likeCnt=:likeCnt and c.cakeId<:cakeId) or (c.likeCnt<:likeCnt) order by c.likeCnt desc,c.cakeId desc")
//    List<Cake> findAllByOrderByIdDesc(@Param("cakeId") Long cakeId, @Param("likeCnt") int likeCnt, Pageable pageable);

    List<Cake> findTop5ByOrderByLikeCnt();
    List<Cake> findTop9ByStoreOrderByCreatedAtDesc(Store store);

}
