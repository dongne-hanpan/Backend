package com.sparta.project.dto.user;

import com.sparta.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private String username;

    private Long id;

    private String nickname;

    public static UserResponseDto of(User user) {
        return new UserResponseDto(
                user.getUsername(),
                user.getId(),
                user.getNickname()
        );
    }
}