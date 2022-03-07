package com.project.makecake.repository;

import com.project.makecake.model.Store;
import com.project.makecake.model.StoreLike;
import com.project.makecake.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
    StoreLike findByStoreAndUser(Store store, User user);
}
