package com.project.makecake.repository;

import com.project.makecake.model.OrderForm;
import com.project.makecake.model.Post;
import com.project.makecake.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderFormRepository extends JpaRepository<OrderForm, Long> {
    Page<OrderForm> findByUserOrderByCreatedAtDesc(User foundUser, Pageable pageable);
}
