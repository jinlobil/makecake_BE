package com.project.makecake.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class PersonalNoti extends Timestamped{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long personalNotiId;

    @ManyToOne
    @JoinColumn(name="receiveUserId")
    private User recieveUser;

    @ManyToOne
    @JoinColumn(name="createUserId")
    private User createUser;

    @ManyToOne
    @JoinColumn(name="notiId")
    private Noti noti;

    @ManyToOne
    @JoinColumn(name="postId")
    private Post post;

    @Column(nullable = false)
    private boolean checked;

    @Builder
    public PersonalNoti(User recieveUser, User createUser, Noti noti, Post post) {
        this.recieveUser = recieveUser;
        this.createUser = createUser;
        this.noti = noti;
        this.post = post;
        this.checked = false;
    }
}
