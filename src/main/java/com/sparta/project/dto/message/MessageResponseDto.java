package com.sparta.project.dto.message;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MessageResponseDto {

    private Long chatId;
    private String profileImage;
    private String nickname;
    private String lastContent;

}
