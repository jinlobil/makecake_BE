package com.project.makecake.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
public class Cake  extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long cakeId;

    @Column
    private String url;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @Column
    private int likeCnt;

    // 생성자
    public Cake(String url,Store store) {
        this.url=url;
        this.store = store;
    }

    // 좋아요
    public boolean like(boolean myLike) {
        if (myLike) {
            this.likeCnt += 1;
            return false;
        } else {
            this.likeCnt -= 1;
            return true;
        }
    }
}
