package com.project.makecake.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StoreLike extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long storeLikeId;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public StoreLike(Store store, User user){
        this.store = store;
        this.user = user;
    }

}
