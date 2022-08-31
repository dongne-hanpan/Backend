package com.sparta.project.controller;

import com.sparta.project.dto.AverageDto;
import com.sparta.project.service.AverageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
public class AverageController {

    private final AverageService averageService;

    @PostMapping("/api/match/result")
    private void inputMyScore(@RequestBody AverageDto averageDto) {
        averageService.inputMyScore(averageDto);
    }

}
