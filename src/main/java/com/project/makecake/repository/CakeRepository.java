package com.project.makecake.repository;

import com.project.makecake.model.Cake;
import com.project.makecake.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CakeRepository extends JpaRepository<Cake, Long> {

    List<Cake> findAllByStore(Store store);
    List<Cake> findTop5ByOrderByLikeCntDesc();
    List<Cake> findTop9ByStoreOrderByCreatedAtDesc(Store store);
    Page<Cake> findAllByStore_StoreId(long storeId, Pageable pageable);
    List<Cake> findAllByStore_StoreId(long storeId);
    void deleteAllByStore_StoreId(Long storeId);

    @Query(value = "SELECT * FROM cake ORDER BY RAND() LIMIT 54", nativeQuery = true)
    List<Cake> findByRandom();

}
