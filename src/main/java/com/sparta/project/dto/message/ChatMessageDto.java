package com.sparta.project.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ChatMessageDto {

    private Long match_id;
    private String sender;
    private String message;
    private String type;
    private long createdAt;

}
