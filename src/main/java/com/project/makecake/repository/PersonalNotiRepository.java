package com.project.makecake.repository;

import com.project.makecake.model.PersonalNoti;
import com.project.makecake.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalNotiRepository extends JpaRepository<com.project.makecake.model.PersonalNoti,Long> {

    List<PersonalNoti> findAllByRecieveUserOrderByCreatedAtDesc(User user);

    // findAllByOrderByCreatedAtDesc
}
