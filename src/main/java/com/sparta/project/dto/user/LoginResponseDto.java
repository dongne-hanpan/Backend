package com.sparta.project.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String grantType;
    private String accessToken;

    private Long userId;
    private String username;
    private String nickname;
    private String profileImage;
    private double mannerPoint;

}