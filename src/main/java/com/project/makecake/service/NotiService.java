package com.project.makecake.service;

import com.project.makecake.dto.NotiRequestDto;
import com.project.makecake.enums.NotiType;
import com.project.makecake.model.Noti;
import com.project.makecake.repository.FixNotiRepository;
import com.project.makecake.repository.NotiRepository;
import com.project.makecake.repository.PersonalNotiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotiService {

    private final NotiRepository notiRepository;
    private final PersonalNotiRepository personalNotiRepository;
    private final FixNotiRepository fixNotiRepository;

    // 알림 등록 메소드
    @Transactional
    public void addNoti(NotiRequestDto requestDto) {

        NotiType type;

        // 알림 타입 확인
        try {
            type = NotiType.valueOf(requestDto.getType().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 알림 타입입니다.");
        }

        // LIKE, COMMENT 알림은 새로 생성 불가
        if(!type.isAddAble()) {
            throw new IllegalArgumentException("LIKE, COMMENT 타입의 알림은 추가할 수 없습니다.");
        }

        // 알림 생성
        Noti noti = new Noti(requestDto);
        // 저장
        notiRepository.save(noti);
    }
}
