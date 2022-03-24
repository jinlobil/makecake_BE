package com.project.makecake.repository;

import com.project.makecake.model.OrderForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderFormRepository extends JpaRepository<OrderForm, Long> {
}
