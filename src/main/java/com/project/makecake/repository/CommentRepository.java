package com.project.makecake.repository;

import com.project.makecake.model.Comment;
import com.project.makecake.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    void deleteAllByPost(Post foundPost);
}
