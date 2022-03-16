package com.project.makecake.model;

import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.enums.DesignState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Design extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long designId;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private String imgName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DesignState state;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    // 생성자
    public Design(ImageInfoDto imgInfo, User user) {
        this.imgUrl = imgInfo.getUrl();
        this.imgName = imgInfo.getName();
        this.state = DesignState.UNPOST;
        this.user = user;
    }

    // 도안이 게시되면 상태 POST로 바꾸기
    public void post() {
        this.state = DesignState.POST;
    }

    // 도안 게시글이 지워지면 상태 UNPOST로 바꾸기
    public void unpost() {
        this.state = DesignState.UNPOST;
    }
}
