package com.project.makecake.model;

import lombok.Data;

import javax.persistence.*;

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
}
