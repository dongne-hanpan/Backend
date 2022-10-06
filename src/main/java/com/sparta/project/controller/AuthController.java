package com.sparta.project.controller;

import com.sparta.project.dto.token.TokenDto;
import com.sparta.project.dto.token.TokenRequestDto;
import com.sparta.project.dto.user.LoginRequestDto;
import com.sparta.project.dto.user.LoginResponseDto;
import com.sparta.project.dto.user.UserRequestDto;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public void signup(@RequestBody UserRequestDto userRequestDto) {
        ResponseEntity.ok(authService.signup(userRequestDto));
    }
    @GetMapping("/username/{username}")
    public boolean existUsername(@PathVariable String username) {
        return !userRepository.existsByUsername(username);
    }

    @GetMapping("/nickname/{nickname}")
    public boolean existNickname(@PathVariable String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }
    @GetMapping("/logout")
    public void logOut(@RequestHeader(value = "Authorization") String token) {
        authService.logout(token);
    }

    @GetMapping("/reissue")
    public LoginResponseDto reissue(@RequestHeader(value = "Authorization") String token) {
        return authService.reissue(token);
    }
    @GetMapping("/refresh")
    public LoginResponseDto refreshUserInfo(@RequestHeader(value = "Authorization") String token) {
        return authService.refreshUserInfo(token);
    }
}