package com.project.makecake.model;

import com.project.makecake.dto.ImageInfoDto;
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
}
