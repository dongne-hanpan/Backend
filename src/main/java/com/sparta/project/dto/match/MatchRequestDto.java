package com.sparta.project.dto.match;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MatchRequestDto {

    private String writer;
    private String date;
    private String time;
    private String place;
    private String placeDetail;
    private String contents;
    private Long region;
    private String sports;
    private Long matchIntakeFull;
    private String matchStatus;

}
