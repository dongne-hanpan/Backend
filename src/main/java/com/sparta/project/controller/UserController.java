package com.sparta.project.controller;

import com.sparta.project.dto.message.MessageResponseDto;
import com.sparta.project.dto.user.CommentDto;
import com.sparta.project.dto.user.EvaluationDto;
import com.sparta.project.dto.user.MyPageResponseDto;
import com.sparta.project.entity.Evaluation;
import com.sparta.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/comment")
    private void evaluateUser(@RequestBody EvaluationDto evaluationDto, @RequestHeader(value = "Authorization") String token) {
        userService.evaluateUser(evaluationDto, token);
    }

    @GetMapping("/mypage/{sports}")
    private MyPageResponseDto myPage(@PathVariable String sports, @RequestHeader(value = "Authorization") String token) {
        return userService.myPage(sports, token);
    }

    @GetMapping("/chat-list")
    private List<MessageResponseDto> myChatList(@RequestHeader(value = "Authorization") String token) {
        return userService.myChatList(token);
    }

    @PostMapping("/upload-image")
    private String uploadProfileImage(@RequestParam("image") MultipartFile multipartFile,
                                      @RequestHeader(value = "Authorization") String token) throws IOException {
        return userService.uploadProfileImage(multipartFile, token);
    }

    @PostMapping("/show-comment")
    private List<CommentDto> showComment(@RequestBody EvaluationDto evaluationDto) {
        return userService.showComment(evaluationDto.getNickname());
    }
}