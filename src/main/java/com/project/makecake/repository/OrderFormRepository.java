package com.project.makecake.repository;

import com.project.makecake.model.OrderForm;
import com.project.makecake.model.Post;
import com.project.makecake.model.Store;
import com.project.makecake.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderFormRepository extends JpaRepository<OrderForm, Long> {
    @Query("SELECT DISTINCT a.store FROM OrderForm a")
    List<Store> findDistinctStore();
}
