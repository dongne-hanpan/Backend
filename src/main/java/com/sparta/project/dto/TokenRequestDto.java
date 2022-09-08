package com.sparta.project.dto;

import com.sparta.project.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
}