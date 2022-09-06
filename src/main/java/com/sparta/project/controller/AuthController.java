package com.sparta.project.controller;

import com.sparta.project.dto.TokenDto;
import com.sparta.project.dto.TokenRequestDto;
import com.sparta.project.dto.UserRequestDto;
import com.sparta.project.dto.UserResponseDto;
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
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(authService.signup(userRequestDto));
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
    public ResponseEntity<TokenDto> login(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(authService.login(userRequestDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}