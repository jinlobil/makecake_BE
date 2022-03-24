package com.project.makecake.model;

import lombok.Builder;
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
    private String redirectUrl;

    @Column(nullable = false)
    private boolean reveal;

    // 생성자
    @Builder
    public FixNoti(Noti noti, String redirectUrl) {
        this.noti = noti;
        this.redirectUrl = redirectUrl;
        this.reveal = true;
    }

    // 고정 알림 내리는 메소드 (reveal을 false로)
    public void editReveal() {
        this.reveal = false;
    }

}
