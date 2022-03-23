package com.project.makecake.model;

import com.project.makecake.dto.NotiContentRequestDto;
import com.project.makecake.dto.NotiRequestDto;
import com.project.makecake.enums.NotiType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Noti {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long notiId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotiType type;

    @Column(nullable = false)
    private String mainContent;

    @Column(nullable = true)
    private String subContent;

    @Builder
    public Noti(NotiRequestDto requestDto) {
        this.type = NotiType.valueOf(requestDto.getType().toUpperCase());
        this.mainContent = requestDto.getMainContent();
        this.subContent = requestDto.getSubContent();
    }

    // 내용 수정 메소드
    public void editContent(NotiContentRequestDto requestDto) {
        this.mainContent = requestDto.getMainContent();
        this.subContent = requestDto.getSubContent();
    }

}
