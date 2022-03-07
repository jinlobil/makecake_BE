package com.project.makecake.model;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class CakeLike extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cakeLikeId;

    @ManyToOne
    @JoinColumn(name="cakeId")
    private Cake cake;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    // 생성자
    public CakeLike(Cake cake, User user) {
        this.cake = cake;
        this.user = user;
    }
}
