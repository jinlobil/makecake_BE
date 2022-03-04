package com.project.makecake.repository;

import com.project.makecake.model.Cake;
import com.project.makecake.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CakeRepository extends JpaRepository<Cake, Long> {
    List<Cake> findAllByStore(Store store);
}
