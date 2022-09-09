package com.sparta.project.service;

import com.sparta.project.dto.BowlingDto;
import com.sparta.project.model.Bowling;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import com.sparta.project.repository.BowlingRepository;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BowlingService {

    private final BowlingRepository bowlingRepository;
    private final ValidationService validationService;
    private final AuthService authService;
    private final CalculateService calculateService;

    public Long inputMyScore(BowlingDto bowlingDto, String token) {

        Match match = validationService.validate(bowlingDto.getMatch_id(), token);

        User user = authService.getUserIdByToken(token);

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
