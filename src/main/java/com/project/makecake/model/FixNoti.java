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

    // 생성자
    public FixNoti(Noti noti) {
        this.noti = noti;
        this.reveal = true;
    }

    // 고정 알림 내리는 메소드 (reveal을 false로)
    public void editReveal() {
        this.reveal = false;
    }

}
