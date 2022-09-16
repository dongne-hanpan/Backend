package com.sparta.project.dto.match;

import com.sparta.project.model.UserListInMatch;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MatchResponseDto {

    private Long match_id;
    private String writer;
    private String date;
    private String time;
    private String place;
    private String placeDetail;
    private String contents;
    private Long region;
    private String sports;
    private Long matchIntakeCnt;
    private Long matchIntakeFull;
    private String profileImage_HOST;
    private double mannerPoint_HOST;
    private String level_HOST;
    private String matchStatus;
    private List<UserListInMatchDto> userListInMatch;
}
