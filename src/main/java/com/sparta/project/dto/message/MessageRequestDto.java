package com.sparta.project.dto.message;

import com.sparta.project.entity.Match;
import com.sparta.project.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDto {

    private String message;
    private Match match;
    private User user;

}
