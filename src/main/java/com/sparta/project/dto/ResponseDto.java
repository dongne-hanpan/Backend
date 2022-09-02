package com.sparta.project.dto;

import com.sparta.project.model.InvitedUser;
import com.sparta.project.model.Match;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseDto {

    private String nickname;
    private Long totalAverage;
    private List<Match> matchList = new ArrayList<>();
    private String profileImage;
    private double mannerPoint;
    private int totalMatchCount;
}
