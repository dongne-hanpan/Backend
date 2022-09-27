package com.sparta.project.dto.user;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class InviteRequestReturnMessageDto {

    private String returnMessage;
    private String profileImage;
    private String hostNickname;
    private String date;
    private String time;
    private String place;
    private Long match_id;

}
