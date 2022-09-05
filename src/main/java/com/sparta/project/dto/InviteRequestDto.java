package com.sparta.project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InviteRequestDto {

    private Long match_id;
    private String nickname;
    private boolean permit;

}
