package com.project.makecake.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class StoreUrl  extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long storeUrlId;

    @Column
    private String url;

    @Column
    private String type;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;
}
