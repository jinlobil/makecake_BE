package com.project.makecake.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Review extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;


    // 생성자
    @Builder
    public Review(String content, Store store, User user){
        this.content = content;
        this.store = store;
        this.user = user;
    }

    // 수정 메소드
    public void edit(String content){
        this.content = content;
    }

}
