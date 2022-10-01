package com.sparta.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.project.dto.user.KakaoLoginResponseDto;
import com.sparta.project.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class KakaoUserController {
    private final KakaoUserService kakaoUserService;

    @GetMapping("/user/kakao/callback")
    public ResponseEntity<KakaoLoginResponseDto> kakaoLogin(@RequestParam String code) throws JsonProcessingException, UnsupportedEncodingException {
        System.out.println(code);
        return ResponseEntity.ok(kakaoUserService.kakaoLogin(code));

    }
}