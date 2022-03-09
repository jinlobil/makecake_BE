package com.project.makecake.repository;

import com.project.makecake.model.Post;
import com.project.makecake.model.PostLike;
import com.project.makecake.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    void deleteByUserAndPost(User user, Post foundPost);


    void deleteAllByPost(Post foundPost);
}
