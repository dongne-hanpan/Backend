package com.sparta.project.dto.message;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class MessageResponseDto {

    private Long chatId;
    private String profileImage;
    private String nickname;
    private String lastContent;

}
