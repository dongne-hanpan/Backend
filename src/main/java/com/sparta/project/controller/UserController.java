package com.sparta.project.controller;

import com.sparta.project.dto.EvaluationDto;
import com.sparta.project.dto.UserResponseDto;
import com.sparta.project.model.InvitedUser;
import com.sparta.project.repository.InvitedUserRepository;
import com.sparta.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    
}