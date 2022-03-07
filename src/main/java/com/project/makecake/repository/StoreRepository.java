package com.project.makecake.repository;

import com.project.makecake.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findTop5ByOrderByLikeCntDesc();
}
