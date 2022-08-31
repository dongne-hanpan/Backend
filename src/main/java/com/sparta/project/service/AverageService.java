package com.sparta.project.service;

import com.sparta.project.dto.AverageDto;
import com.sparta.project.model.Average;
import com.sparta.project.repository.AverageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AverageService {

    private final AverageRepository averageRepository;
    private final MatchService matchService;

    public void inputMyScore(AverageDto averageDto) {

//        matchService.validate(averageDto.getMatch_id());

        averageRepository.save(Average.builder()
                .average(averageDto.getAverage())
//                .user(user)
//                .match(match)
                .build());
    }

}
