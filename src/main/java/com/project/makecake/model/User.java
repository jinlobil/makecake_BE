package com.project.makecake.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class User {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long userId;

    @Column
    private String username;

    @Column
    private String nickname;

    @Column
    private String password;
}
