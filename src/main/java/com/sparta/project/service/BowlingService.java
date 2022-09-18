package com.sparta.project.service;

import com.sparta.project.dto.sports.BowlingDto;
import com.sparta.project.entity.Bowling;
import com.sparta.project.entity.Match;
import com.sparta.project.entity.User;
import com.sparta.project.repository.BowlingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BowlingService {

    private final BowlingRepository bowlingRepository;
    private final ValidationService validationService;
    private final AuthService authService;
    private final CalculateService calculateService;

    public Long inputMyScore(BowlingDto bowlingDto, String token) {

        Match match = validationService.validate(bowlingDto.getMatch_id(), token);

        User user = authService.getUserByToken(token);

        if(!bowlingRepository.existsByUserAndMatch(user, match) && bowlingDto.getMyScore() <= 300 && bowlingDto.getMyScore() >= 0) {
            bowlingRepository.save(Bowling.builder()
                    .myScore(bowlingDto.getMyScore())
                    .user(user)
                    .match(match)
                    .build());

            return calculateService.calculateAverageScore(user);
        }
        return 0L;
    }
}
