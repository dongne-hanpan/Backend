package com.sparta.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class InviteResponseDto {

    private String nickname;
    private double mannerPoint;
    private Long averageScore;
    private int matchCount;
    private Long match_id;
}
