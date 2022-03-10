package com.project.makecake.repository;

import com.project.makecake.model.Post;
import com.project.makecake.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByUser(User user);
}
