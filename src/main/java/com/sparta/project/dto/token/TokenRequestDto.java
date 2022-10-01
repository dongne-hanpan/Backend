package com.sparta.project.dto.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
}