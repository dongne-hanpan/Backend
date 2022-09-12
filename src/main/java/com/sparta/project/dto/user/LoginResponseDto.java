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

    //로그인한 유저의 정보
    private String username;
    private String nickname;
    private String profileImage;
    private double mannerPoint;

}