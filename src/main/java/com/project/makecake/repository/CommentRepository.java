package com.project.makecake.repository;

import com.project.makecake.model.Comment;
import com.project.makecake.model.Post;
import com.project.makecake.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    void deleteAllByPost(Post foundPost);
    Page<Comment> findByUser(User user, Pageable pageable);

    Long countByPost(Post foundPost);

    Page<Comment> findAllByPost(Post foundPost, Pageable pageable);
}
