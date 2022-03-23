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

        @Builder
        public Personal(PersonalNoti personalNoti, String mainContent, String timeDiff) {
            Noti noti = personalNoti.getNoti();
            this.type = noti.getType().toString();
            this.mainContent = mainContent;
            this.subContent = noti.getSubContent();
            this.checked = personalNoti.isChecked();
            this.postId = personalNoti.getPost().getPostId();
            this.timeDiff = timeDiff;
        }
    }
}
