package com.sparta.project.dto.token;

import com.sparta.project.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder

public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
}