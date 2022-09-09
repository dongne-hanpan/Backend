package com.sparta.project.dto;

import com.sparta.project.model.Authority;
import com.sparta.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp = "^(?=.*[a-zA-z])(?=.*[0-9])(?!.*[^a-zA-z0-9]).{4,12}$",
            message = "닉네임은 4~12 자리이면서 1개 이상의 알파벳, 숫자를 포함해야합니다.")
    private String username;



    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])(?!.*[^a-zA-z0-9]).{4,32}$",
            message = "비밀번호는 4~32 자리이면서 1개 이상의 알파벳, 숫자를 포함해야합니다.")
    private String password;
    private String nickname;
    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .nickname(nickname)
                .username(username)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .build();
    }
}