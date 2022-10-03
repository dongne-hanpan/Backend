package com.sparta.project.dto.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationDto {

    private String receiver;

    private String alarmType;

    private Boolean isRead;

    private Long match_id;
    private String match_date;

    private String sender;
    private String sender_ProfileImage;
    private double sender_MannerPoint;
    private String sender_Level;
    private Long sender_AverageScore;
    private int sender_MatchCnt;

}

