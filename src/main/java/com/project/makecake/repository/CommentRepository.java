package com.project.makecake.repository;

import com.project.makecake.model.Comment;
import com.project.makecake.model.Post;
import com.project.makecake.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    void deleteAllByPost(Post foundPost);
    List<Comment> findAllByPost(Post foundPost);
    List<Comment> findByUser(User user);
}
