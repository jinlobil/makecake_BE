package com.project.makecake.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
public class User  extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private String userPicture;

    @Column
    private String providerEmail;

    @Column
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column
    private String provider;

    @Column
    private String providerId;

    @Builder
    public User(String username, String nickname, String password, String userPicture, String providerEmail, UserRoleEnum role, String provider, String providerId){
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.userPicture = userPicture;
        this.providerEmail = providerEmail;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
}
