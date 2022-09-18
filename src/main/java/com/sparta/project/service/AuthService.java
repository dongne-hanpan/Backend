package com.sparta.project.service;

import com.sparta.project.dto.token.TokenDto;
import com.sparta.project.dto.user.LoginRequestDto;
import com.sparta.project.dto.user.LoginResponseDto;
import com.sparta.project.dto.user.UserRequestDto;
import com.sparta.project.dto.user.UserResponseDto;
import com.sparta.project.entity.RefreshToken;
import com.sparta.project.entity.User;
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
    private final CalculateService calculateService;

    @Transactional
    public UserResponseDto signup(UserRequestDto userRequestDto) {
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        User user = userRequestDto.toUser(passwordEncoder);

        return UserResponseDto.of(userRepository.save(user));
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow();

        UsernamePasswordAuthenticationToken authenticationToken = loginRequestDto.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        refreshTokenRepository.save(RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build());

        return LoginResponseDto.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .mannerPoint(calculateService.calculateMannerPoint(user))
                .profileImage(null)
                .build();
    }

    @Transactional
    public void logout(String token) {
        User user = getUserByToken(token);
        refreshTokenRepository.deleteByKey(user.getId().toString());
    }

    public User getUserByToken(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token.substring(7));
        Long user_id = Long.parseLong(authentication.getName());
        return userRepository.findById(user_id).orElseThrow(() ->
                new IllegalArgumentException("유저 정보가 없습니다."));
    }

    public LoginResponseDto refreshUserInfo(String token) {

        User user = getUserByToken(token);

        return LoginResponseDto.builder()
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .mannerPoint(calculateService.calculateMannerPoint(user))
                .nickname(user.getNickname())
                .build();
    }

//    @Transactional
//    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
//        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
//            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
//        }
//
//        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
//
//        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
//                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));
//
//        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
//            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
//        }
//
//        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
//
//        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
//        refreshTokenRepository.save(newRefreshToken);
//
//        return tokenDto;
//    }
}