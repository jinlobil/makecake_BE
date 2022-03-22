package com.project.makecake.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Comment extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name="postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    // 생성자
    public Comment(String content, Post post, User user){
        this.content = content;
        this.post = post;
        this.user = user;
    }

    // 수정 메소드
    public void edit(String content) {
        this.content = content;
    }

    // 연관관계 삭제 메소드
    public void deleteRelation() {
        this.post = null;
        this.user = null;
    }
}
