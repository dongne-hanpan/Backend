package com.sparta.project.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentDto {

    private String comment;
    private String nickname;
    private String profileImage;
    private String writer;
}
