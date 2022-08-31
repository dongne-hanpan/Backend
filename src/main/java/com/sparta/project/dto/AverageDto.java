package com.sparta.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AverageDto {

    private Long average;
    private Long user_id;
    private Long match_id;

}
