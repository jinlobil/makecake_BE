package com.project.makecake.repository;

import com.project.makecake.model.Design;
import com.project.makecake.model.DesignState;
import com.project.makecake.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignRepository extends JpaRepository<Design,Long> {
    Page<Design> findByUserAndState(User user, DesignState UNPOST , Pageable pageable);
}
