package com.sparta.project.dto;

import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequestDto {

    private String message;
    private Match match;
    private User user;

}