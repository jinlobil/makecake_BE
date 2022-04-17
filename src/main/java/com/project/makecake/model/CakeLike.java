package com.project.makecake.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class CakeLike extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cakeLikeId;

    @ManyToOne
    @JoinColumn(name="cakeId")
    private Cake cake;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @Builder
    public CakeLike(Cake cake, User user) {
        this.cake = cake;
        this.user = user;
    }

}
