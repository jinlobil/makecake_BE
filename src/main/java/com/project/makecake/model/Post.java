package com.project.makecake.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Post extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long postId;

    @OneToOne
    @JoinColumn(name="designId")
    private Design design;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @Column(nullable = false)
    private String Content;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String shape;

    @Column(nullable = true)
    private String purpose;

    @Column(nullable = false)
    private boolean isMade;

    @ManyToOne
    @JoinColumn(name="storeId", nullable = true)
    private Store store;

    @Column
    private int viewCnt;

    @Column
    private int likeCnt;

    @Column
    private int commentCnt;

    // 생성자


}
