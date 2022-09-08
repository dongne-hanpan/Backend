package com.sparta.project.service;

import com.sparta.project.dto.TokenDto;
import com.sparta.project.dto.TokenRequestDto;
import com.sparta.project.dto.UserRequestDto;
import com.sparta.project.dto.UserResponseDto;
import com.sparta.project.model.RefreshToken;
import com.sparta.project.model.User;
import com.sparta.project.repository.RefreshTokenRepository;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserResponseDto signup(UserRequestDto userRequestDto) {
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        User user = userRequestDto.toUser(passwordEncoder);

//        user.setTotalAverage();

        return UserResponseDto.of(userRepository.save(user));
    }

    @Transactional
    public TokenDto login(UserRequestDto userRequestDto) {

        User user = userRepository.findByUsername(userRequestDto.getUsername()).orElseThrow();

        UsernamePasswordAuthenticationToken authenticationToken = userRequestDto.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        tokenDto.setUser(user);

        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }
}