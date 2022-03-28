package com.project.makecake.repository;

import com.project.makecake.model.Cake;
import com.project.makecake.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CakeRepository extends JpaRepository<Cake, Long> {
    List<Cake> findAllByStore(Store store);
    List<Cake> findTop5ByOrderByLikeCntDesc();
    List<Cake> findTop9ByStoreOrderByCreatedAtDesc(Store store);
    Page<Cake> findAllByStore_StoreId(long storeId, Pageable pageable);

    void deleteAllByStore_StoreId(Long storeId);




    // 케이크 리스트 처음 15개
//    @Query(value = "select c from Cake as c order by c.likeCnt desc, c.cakeId desc")
//    List<Cake> findCakeListFirst(Pageable pageable);

    // 케이크 리스트 다음 9개씩
//    @Query(value = "select c from Cake as c where (c.likeCnt=:likeCnt and c.cakeId<:cakeId) or (c.likeCnt<:likeCnt) order by c.likeCnt desc,c.cakeId desc")
//    List<Cake> findCakeListMore(@Param("cakeId") Long cakeId, @Param("likeCnt") int likeCnt, Pageable pageable);
}
