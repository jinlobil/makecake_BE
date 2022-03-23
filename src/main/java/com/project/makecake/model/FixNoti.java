package com.project.makecake.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class FixNoti extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long fixNotiId;

    @ManyToOne
    @JoinColumn(name="notiId")
    private Noti noti;

    @Column(nullable = false)
    private boolean reveal;

}
