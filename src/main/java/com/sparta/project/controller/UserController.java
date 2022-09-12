package com.sparta.project.controller;

import com.sparta.project.dto.user.EvaluationDto;
import com.sparta.project.dto.user.MyPageResponseDto;
import com.sparta.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/comment")
    private String evaluateUser(@RequestBody EvaluationDto evaluationDto, @RequestHeader(value = "Authorization") String token) {
        userService.evaluateUser(evaluationDto, token);
        return "평가완료";
    }

    @GetMapping("/mypage/{sports}")
    private MyPageResponseDto myPage(@PathVariable String sports, @RequestHeader(value = "Authorization") String token) {
            return userService.myPage(sports, token);
        }
}