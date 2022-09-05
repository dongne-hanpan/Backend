package com.sparta.project.dto;

import lombok.Data;

@Data
public class MatchDto {

    private String writer;
    private String title;
    private String contents;
    private Long region;
    private String sports;
}
