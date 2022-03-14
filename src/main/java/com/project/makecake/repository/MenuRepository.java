package com.project.makecake.repository;

import com.project.makecake.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByStore_StoreId(long storeId);
    void deleteAllByStore_StoreId(long storeId);
}
