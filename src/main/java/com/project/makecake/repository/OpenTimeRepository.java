package com.project.makecake.repository;

import com.project.makecake.model.OpenTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OpenTimeRepository extends JpaRepository<OpenTime, Long> {
    void deleteAllByStore_StoreId(Long storeId);

    List<OpenTime> findAllByStore_StoreId(Long storeId);
}
