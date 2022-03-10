package com.project.makecake.repository;

import com.project.makecake.model.Design;
import com.project.makecake.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignRepository extends JpaRepository<Design,Long> {
    List<Design> findByStateContainingAndUser(String state, User user);
    List<Design> findByUserAndStateIsNull(User user);
}
