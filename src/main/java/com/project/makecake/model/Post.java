package com.project.makecake.model;

import com.project.makecake.requestDto.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Post extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long postId;

    @OneToOne
    @JoinColumn(name="designId")
    private Design design;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String shape;

    @Column(nullable = true)
    private String purpose;

    @Column(nullable = false)
    private boolean isMade;

    @ManyToOne
    @JoinColumn(name="storeId", nullable = true)
    private Store store;

    @Column
    private int viewCnt;

    @Column
    private int likeCnt;

    @Column
    private int commentCnt;

    // 제작한 매장 이름이 없는 경우의 생성자
    public Post(PostRequestDto requestDto, User user, Design design) {
        this.design = design;
        this.user = user;
        this.content = requestDto.getContent();
        this.size = requestDto.getSize();
        this.shape = requestDto.getShape();
        this.purpose = requestDto.getPurpose();
        this.isMade = requestDto.isMade();
        this.viewCnt = 0;
        this.likeCnt = 0;
        this.commentCnt = 0;
    }

    // 제작한 매장 이름이 있는 경우의 생성자
    public Post(PostRequestDto requestDto, User user, Design design, Store store) {
        this.design = design;
        this.user = user;
        this.content = requestDto.getContent();
        this.size = requestDto.getSize();
        this.shape = requestDto.getShape();
        this.purpose = requestDto.getPurpose();
        this.isMade = requestDto.isMade();
        this.store = store;
        this.viewCnt = 0;
        this.likeCnt = 0;
        this.commentCnt = 0;
    }

    // 제작한 매장 이름이 없는 경우의 수정 메소드
    public void update(PostRequestDto requestDto) {
        this.content = requestDto.getContent();
        this.size = requestDto.getSize();
        this.shape = requestDto.getShape();
        this.purpose = requestDto.getPurpose();
        this.isMade = requestDto.isMade();
    }

    // 제작한 매장 이름이 있는 경우의 수정 메소드
    public void update(PostRequestDto requestDto, Store store) {
        this.content = requestDto.getContent();
        this.size = requestDto.getSize();
        this.shape = requestDto.getShape();
        this.purpose = requestDto.getPurpose();
        this.isMade = requestDto.isMade();
        this.store = store;
    }

    // 연관관계 삭제 메소드
    public void deleteRelation() {
        this.store = null;
        this.design = null;
    }

    // 좋아요
    public boolean likePost(boolean myLike) {
        if (myLike) {
            this.likeCnt += 1;
            return false;
        } else {
            this.likeCnt -= 1;
            return true;
        }
    }
}
