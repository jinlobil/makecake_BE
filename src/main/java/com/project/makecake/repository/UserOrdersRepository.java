package com.project.makecake.repository;

import com.project.makecake.model.Design;
import com.project.makecake.model.User;
import com.project.makecake.model.UserOrders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrdersRepository extends JpaRepository<UserOrders, Long> {

    Page<UserOrders> findByUserOrderByCreatedAtDesc(User foundUser, Pageable pageable);
    void deleteByDesign(Design design);

}
