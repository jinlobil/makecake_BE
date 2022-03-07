package com.project.makecake.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter

@DynamicUpdate
public class Store extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long storeId;

    @Column
    private Long naverId;

    @Column(nullable = false)
    private String name;

    @Column()
    private String description;

    @Column
    private float x;

    @Column
    private float y;

    @Column
    private String fullAddress;

    @Column
    private String roadAddress;

    @Column
    private String mainImg;

    @Column
    private String openTimeString;

    @Column
    private String phone;

    @OneToMany(mappedBy = "store")
    private List<StoreUrl> storeUrlList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Cake> cakeList = new ArrayList<>();

    @Column(columnDefinition = "integer default 0")
    private int likeCnt;
}