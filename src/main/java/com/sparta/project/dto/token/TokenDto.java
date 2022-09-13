package com.sparta.project.dto.token;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresIn;
}
