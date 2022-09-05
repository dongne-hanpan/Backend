package com.sparta.project.controller;

import com.sparta.project.dto.EvaluationDto;
import com.sparta.project.dto.MyPageResponseDto;
import com.sparta.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/comment")
    private String evaluateUser(@RequestBody EvaluationDto evaluationDto) {
        userService.evaluateUser(evaluationDto);
        return "평가완료";
    }

    @GetMapping("/mypage/{sports}")
    private MyPageResponseDto myPage(@PathVariable String sports) {
            return userService.myPage(sports);
        }
}