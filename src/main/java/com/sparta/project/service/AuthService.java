package com.sparta.project.service;

import com.amazonaws.services.kms.model.InvalidGrantTokenException;
import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.project.dto.token.TokenDto;
import com.sparta.project.dto.token.TokenRequestDto;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            throw new IllegalArgumentException("이미 가입된 아이디 입니다");
        }

        User user = userRequestDto.toUser(passwordEncoder);

        return UserResponseDto.of(userRepository.save(user));
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("회원 정보가 일치하지 않습니다.");
        }

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
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .mannerPoint(calculateService.calculateMannerPoint(user))
                .profileImage(user.getProfileImage())
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
                new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));
    }

    public LoginResponseDto refreshUserInfo(String token) {

        User user = getUserByToken(token);

        return LoginResponseDto.builder()
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .mannerPoint(calculateService.calculateMannerPoint(user))
                .nickname(user.getNickname())
                .userId(user.getId())
                .build();
    }

    @Transactional
    public LoginResponseDto reissue(String token) {

        Authentication authentication = tokenProvider.getAuthentication(token.substring(7));

        User user = userRepository.findById(Long.parseLong(authentication.getName())).orElseThrow();

        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그아웃 된 사용자입니다."));

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return LoginResponseDto.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .mannerPoint(calculateService.calculateMannerPoint(user))
                .build();
    }
}