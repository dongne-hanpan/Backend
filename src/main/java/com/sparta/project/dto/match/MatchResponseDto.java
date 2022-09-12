package com.sparta.project.dto.match;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchResponseDto {

    private String writer;
    private String date;
    private String time;
    private String place;
    private String contents;
    private Long region;
    private String sports;
    private Long max_user;
    private String profileImage_HOST;
    private double mannerPoint_HOST;
    private String level_HOST;
}
