package com.project.makecake.dto;

import com.project.makecake.model.Noti;
import com.project.makecake.model.PersonalNoti;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class NotiResponseDto {

    private List<Fix> fixNotiList;
    private List<Personal> personalNotiList;

    @Getter
    public static class Fix {
        private String type,mainContent,subContent;

        // 생성자
        public Fix(Noti noti) {
            this.type = noti.getType().toString();
            this.mainContent = noti.getMainContent();
            this.subContent = noti.getSubContent();
        }
    }

    @Getter
    public static class Personal {
        private String type,mainContent,subContent,timeDiff;
        private boolean checked;
        private long postId;

        // 광고, 공지 알림인 경우 생성자
        @Builder(builderMethodName = "nonEditBuilder", buildMethodName = "nonEditBuild")
        public Personal(PersonalNoti personalNoti, String timeDiff) {
            Noti noti = personalNoti.getNoti();
            this.type = noti.getType().toString();
            this.mainContent = noti.getMainContent();
            this.subContent = noti.getSubContent();
            this.checked = personalNoti.isChecked();
            this.timeDiff = timeDiff;
        }

        // 좋아요, 댓글 알림인 경우 생성자
        @Builder(builderMethodName = "editBuilder", buildMethodName = "editBuild")
        public Personal(PersonalNoti personalNoti, String editedMainContent, String timeDiff) {
            Noti noti = personalNoti.getNoti();
            this.type = noti.getType().toString();
            this.mainContent = editedMainContent;
            this.subContent = noti.getSubContent();
            this.checked = personalNoti.isChecked();
            this.postId = personalNoti.getPost().getPostId();
            this.timeDiff = timeDiff;
        }
    }
}
