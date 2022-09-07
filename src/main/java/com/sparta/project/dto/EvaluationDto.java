package com.sparta.project.dto;


import lombok.Getter;

@Getter
public class EvaluationDto {

    private String nickname; // 평가받는 사람의 닉네임.
    private String comment;
    private double mannerPoint;
    private Long match_id;
}
