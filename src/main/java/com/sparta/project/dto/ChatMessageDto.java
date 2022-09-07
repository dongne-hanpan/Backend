package com.sparta.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatMessageDto {

    private Long match_id; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지

}
