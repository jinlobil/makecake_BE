package com.project.makecake.repository;

import com.project.makecake.model.CakeMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CakeMenuRepository extends JpaRepository<CakeMenu, Long> {
}
