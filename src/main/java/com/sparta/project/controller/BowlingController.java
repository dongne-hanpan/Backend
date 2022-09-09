package com.sparta.project.controller;

import com.sparta.project.dto.BowlingDto;
import com.sparta.project.service.BowlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
public class BowlingController {

    private final BowlingService bowlingService;

    @PostMapping("/api/bowling/result")
    private Long inputMyScore(@RequestBody BowlingDto bowlingDto, @RequestHeader(value = "Authorization") String token) {
        return bowlingService.inputMyScore(bowlingDto, token);
    }

}
