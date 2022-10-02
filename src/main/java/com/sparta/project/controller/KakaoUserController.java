package com.sparta.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.project.dto.user.KakaoLoginResponseDto;
import com.sparta.project.service.KakaoUserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class KakaoUserController {
    private final KakaoUserService kakaoUserService;

    @GetMapping("/user/kakao/callback")
    public ResponseEntity<KakaoLoginResponseDto> kakaoLogin(@RequestParam String code) throws JsonProcessingException, UnsupportedEncodingException {
        return ResponseEntity.ok(kakaoUserService.kakaoLogin(code));

    }
}