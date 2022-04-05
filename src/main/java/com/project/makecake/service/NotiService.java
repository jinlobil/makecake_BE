package com.project.makecake.service;

import com.project.makecake.dto.noti.NewNotiResponseDto;
import com.project.makecake.dto.noti.NotiResponseDto;
import com.project.makecake.model.FixNoti;
import com.project.makecake.model.PersonalNoti;
import com.project.makecake.model.User;
import com.project.makecake.repository.FixNotiRepository;
import com.project.makecake.repository.NotiRepository;
import com.project.makecake.repository.PersonalNotiRepository;
import com.project.makecake.repository.UserRepository;
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

    private final PersonalNotiRepository personalNotiRepository;
    private final FixNotiRepository fixNotiRepository;

    // 새로운 알림 여부 조회 메소드
    public NewNotiResponseDto getNewNoti(UserDetailsImpl userDetails) {

        // 비회원이면 new값을 false로 return
        if (userDetails == null) {
            return new NewNotiResponseDto(true);
        }

        User user = userDetails.getUser();

        // 체크하지 않은 PersonalNoti 존재 여부
        boolean existsNewNoti = personalNotiRepository.existsByRecieveUserAndChecked(user,false);

        return new NewNotiResponseDto(!existsNewNoti);

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
            NotiResponseDto.Fix responseDto = new NotiResponseDto.Fix(fixNoti);
            fixNotiResponseDtoList.add(responseDto);
        }

        return fixNotiResponseDtoList;
    }

    // PersonalNoti 찾기 메소드
    public List<NotiResponseDto.Personal> getPersonalNotiList(User user) throws ParseException {

        // PersonalNoti 찾기
        List<PersonalNoti> foundPersonalNotiList = personalNotiRepository.findTop30ByRecieveUserOrderByCreatedAtDesc(user);

        // personalNoti들을 DTO에 담기
        List<NotiResponseDto.Personal> personalNotiResponseDtoList = new ArrayList<>();

        for (PersonalNoti personalNoti : foundPersonalNotiList) {

            // 알림 생성 시간 계산
            String timeDiff = calculateTimeDiff(personalNoti);

            // DTO 생성
            NotiResponseDto.Personal responseDto = addPersonalNotiResponseDto(personalNoti,timeDiff);

            // DTO 리스트에 추가
            personalNotiResponseDtoList.add(responseDto);

            // 알림 읽음으로 변경
            personalNoti.editChecked();
        }

        // 최신 30개에 포함되지 않은 알림은 자동 읽음 처리
        List<PersonalNoti> foundUncheckedPersonalNotiList = personalNotiRepository.findAllByRecieveUserAndChecked(user,false);
        for (PersonalNoti personalNoti : foundUncheckedPersonalNotiList) {
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
            timeDiff = diff+"분전";
        } else if (diff<60*24) {
            timeDiff = diff/60+"시간전";
        } else {
            timeDiff = diff/(60*24)+"일전";
        }

        return timeDiff;
    }

    // PersonalNoti DTO 생성
    public NotiResponseDto.Personal addPersonalNotiResponseDto(PersonalNoti personalNoti, String timeDiff) {

        // mainContent의 수정이 필요한 경우
        if (personalNoti.getNoti().getType().isNeedMessageEdit()) {

            // 알림 메인 내용에 유저의 닉네임 반영
            String editedMainContent = editMainContent(personalNoti);

            // DTO 생성
            return NotiResponseDto.Personal.editBuilder()
                    .personalNoti(personalNoti)
                    .editedMainContent(editedMainContent)
                    .timeDiff(timeDiff)
                    .editBuild();

        // mainContent을 수정하지 않는 경우
        } else {
            // DTO 생성
            return NotiResponseDto.Personal.nonEditBuilder()
                    .personalNoti(personalNoti)
                    .timeDiff(timeDiff)
                    .nonEditBuild();
        }
    }

    // 알림 메인 내용 수정 메소드
    public String editMainContent(PersonalNoti personalNoti) {

        // 알림의 메인 내용
        String mainContent = personalNoti.getNoti().getMainContent();

        // createUser의 닉네임 넣어서 반환
        mainContent = mainContent.replace("{닉네임}",personalNoti.getCreateUser().getNickname());

        return mainContent;
    }
}
