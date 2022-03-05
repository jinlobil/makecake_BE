package com.project.makecake.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Menu  extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long menuId;

    @Column
    private String name;

    @Column
    private String price;

    @Column
    private Boolean changes;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;



}
