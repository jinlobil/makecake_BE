package com.project.makecake.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Cake extends Timestamped{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long cakeId;

    @Column
    private String url;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @Column
    private int likeCnt;

    @Builder
    public Cake(String url, Store store) {
        this.url = url;
        this.store = store;
    }

    // 좋아요수 변경 메소드
    public boolean editLikeCnt(boolean myLike) {
        if (myLike) {
            this.likeCnt += 1;
            return false;
        } else {
            this.likeCnt -= 1;
            return true;
        }
    }
}
