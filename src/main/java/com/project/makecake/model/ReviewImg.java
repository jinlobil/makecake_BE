package com.project.makecake.model;

import com.project.makecake.dto.ImageInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReviewImg extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewImgId;

    @Column(nullable = false)
    private String imgUrl;

    //도안 이름이 필요
    @Column(nullable = false)
    private String imgName;

    @ManyToOne
    @JoinColumn(name = "REVIEW_ID")
    private Review review;

    //생성자
    public ReviewImg(ImageInfoDto imgInfo, Review review){
        this.imgUrl = imgInfo.getUrl();
        this.imgName = imgInfo.getName();
        this.review= review;
    }
}
