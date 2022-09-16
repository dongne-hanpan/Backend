package com.sparta.project.controller;

import com.sparta.project.dto.user.LoginRequestDto;
import com.sparta.project.dto.user.LoginResponseDto;
import com.sparta.project.dto.user.UserRequestDto;
import com.sparta.project.model.User;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.service.AuthService;
import lombok.Getter;
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
    public String signup(@RequestBody UserRequestDto userRequestDto) {
        try {
            ResponseEntity.ok(authService.signup(userRequestDto));
            return "회원가입 완료";
        }catch (Exception e) {
            return "회원가입 실패";
        }
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
        //로그인 실패시 에러반환 필요
    }

    @GetMapping("/logout")
    public void logOut(@RequestHeader(value = "Authorization") String token) {
        authService.logout(token);
    }

//    @PostMapping("/reissue")
//    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
//        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
//    }

    @GetMapping("/refresh")
    public LoginResponseDto refreshUserInfo(@RequestHeader(value = "Authorization") String token) {
        return authService.refreshUserInfo(token);
    }
}