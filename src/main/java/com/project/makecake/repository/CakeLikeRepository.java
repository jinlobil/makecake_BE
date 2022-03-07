package com.project.makecake.repository;

import com.project.makecake.model.CakeLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CakeLikeRepository extends JpaRepository<CakeLike, Long> {
    void deleteByUserIdAndCakeId(Long userId, Long cakeId);

    Optional<CakeLike> findByUserIdAndCakeId(Long userId, Long cakeId);
}
