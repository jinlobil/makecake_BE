package com.project.makecake.service;

import com.project.makecake.dto.NotiRequestDto;
import com.project.makecake.dto.NotiResponseDto;
import com.project.makecake.enums.NotiType;
import com.project.makecake.model.FixNoti;
import com.project.makecake.model.Noti;
import com.project.makecake.model.PersonalNoti;
import com.project.makecake.model.User;
import com.project.makecake.repository.FixNotiRepository;
import com.project.makecake.repository.NotiRepository;
import com.project.makecake.repository.PersonalNotiRepository;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            throw new IllegalArgumentException("추가할 수 없는 알림타입입니다.");
        }

        // 알림 생성
        Noti noti = new Noti(requestDto);
        // 저장
        notiRepository.save(noti);
    }

    // 고정 알림 띄우는 메소드
    @Transactional
    public void addFixNoti(long notiId) {

        // 알림 찾기
        Noti foundNoti = notiRepository.findById(notiId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 알림입니다."));

        // 알림 타입 확인
        if(!foundNoti.getType().isFixAble()) {
            throw new IllegalArgumentException("고정할 수 없는 알림타입입니다.");
        }

        // 고정 알림 생성
        FixNoti fixNoti = new FixNoti(foundNoti);
        fixNotiRepository.save(fixNoti);
    }

    // 고정 알림 내리는 메소드
    @Transactional
    public void editFixNoti(long fixNotiId) {

        // 알림 찾기
        FixNoti foundFixNoti = fixNotiRepository.findById(fixNotiId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 고정알림입니다."));

        // 고정 알림 내림
        foundFixNoti.editReveal();
    }

    // 알림 조회 메소드
    @Transactional
    public NotiResponseDto getNotiList(UserDetailsImpl userDetails) throws ParseException {

        User user = userDetails.getUser();

        // FixNoti 찾기
        List<NotiResponseDto.Fix> fixNotiResponseDtoList = getFixNotiList();

        // PersonalNoti 찾기
        List<NotiResponseDto.Personal> personalNotiResponseDtoList = getPersonalNotiList(user);

        return NotiResponseDto.builder()
                .fixNotiList(fixNotiResponseDtoList)
                .personalNotiList(personalNotiResponseDtoList)
                .build();

    }

    // FixNoti 찾기 메소드
    public List<NotiResponseDto.Fix> getFixNotiList() {

        // reveal값이 true인 FixNoti 찾기
        List<FixNoti> foundFixNotiList = fixNotiRepository.findAllByReveal(true);

        // 반환 DTO에 담기
        List<NotiResponseDto.Fix> fixNotiResponseDtoList = new ArrayList<>();
        for (FixNoti fixNoti : foundFixNotiList) {
            NotiResponseDto.Fix responseDto = new NotiResponseDto.Fix(fixNoti.getNoti());
            fixNotiResponseDtoList.add(responseDto);
        }

        return fixNotiResponseDtoList;
    }

    // PersonalNoti 찾기 메소드
    public List<NotiResponseDto.Personal> getPersonalNotiList(User user) throws ParseException {

        // PersonalNoti 찾기
        List<PersonalNoti> foundPersonalNotiList = personalNotiRepository.findAllByRecieveUserOrderByCreatedAtDesc(user);

        // personalNoti DTO에 담기
        List<NotiResponseDto.Personal> personalNotiResponseDtoList = new ArrayList<>();
        for (PersonalNoti personalNoti : foundPersonalNotiList) {

            // 시간 계산
            String timeDiff = calculateTimeDiff(personalNoti);

            // 알림 메인 내용에 유저의 닉네임 반영
            String mainContent = editMainContent(personalNoti);

            NotiResponseDto.Personal responseDto = NotiResponseDto.Personal.builder()
                    .personalNoti(personalNoti)
                    .timeDiff(timeDiff)
                    .mainContent(mainContent)
                    .build();

            personalNotiResponseDtoList.add(responseDto);

            personalNoti.editChecked();
        }

        return personalNotiResponseDtoList;
    }

    // 시간 차이 계산 메소드
    public String calculateTimeDiff(PersonalNoti personalNoti) throws ParseException {
        Date nowDateTime = new Date();

        String createdAt = personalNoti.getCreatedAt();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date createdDateTime = formatter.parse(createdAt);

        long diff = (nowDateTime.getTime()-createdDateTime.getTime())/60000;

        String timeDiff;
        if (diff<60) {
            timeDiff = diff+"분 전";
        } else {
            diff = diff/60;
            timeDiff = diff+"시간 전";
        }

        return timeDiff;
    }

    // 알림 메인 내용 수정 메소드
    public String editMainContent(PersonalNoti personalNoti) {

        // 알림의 메인 내용
        String mainContent = personalNoti.getNoti().getMainContent();

        // 알림 타입이 메세지 수정 가능이면 닉네임 반영한 메세지로 수정
        if (personalNoti.getNoti().getType().isMessageEditAble()) {
            mainContent = mainContent.replace("{닉네임}",personalNoti.getCreateUser().getNickname());
        }

        return mainContent;
    }
}
