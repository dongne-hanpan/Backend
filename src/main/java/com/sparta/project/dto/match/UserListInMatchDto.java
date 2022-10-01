package com.sparta.project.dto.match;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserListInMatchDto {

    private String profileImage;
    private String nickname;

}
