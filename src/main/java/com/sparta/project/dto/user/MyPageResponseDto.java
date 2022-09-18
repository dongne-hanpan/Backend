package com.sparta.project.dto.user;

import com.sparta.project.dto.match.MatchResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class  MyPageResponseDto {

    private String nickname;
    private Long score;
    private List<MatchResponseDto> matchList;
    private String profileImage;
    private double mannerPoint;
    private int matchCount;
    private List<String> comment;
}
