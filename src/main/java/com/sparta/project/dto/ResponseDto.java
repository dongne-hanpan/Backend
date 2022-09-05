package com.sparta.project.dto;

import com.sparta.project.model.Evaluation;
import com.sparta.project.model.Match;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseDto {

    private String nickname;
    private Long score;
    private List<Match> matchList = new ArrayList<>();
    private String profileImage;
    private double mannerPoint;
    private int matchCount;
    private List<String> comment = new ArrayList<>();
}
