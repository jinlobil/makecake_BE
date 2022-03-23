package com.project.makecake.repository;

import com.project.makecake.model.FixNoti;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixNotiRepository extends JpaRepository<FixNoti,Long> {

    List<FixNoti> findAllByReveal(boolean b);
}
