package com.project.makecake.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StoreLike  extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeLikeId;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

}
