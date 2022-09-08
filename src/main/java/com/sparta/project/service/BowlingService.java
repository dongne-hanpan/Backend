package com.sparta.project.service;

import com.sparta.project.dto.BowlingDto;
import com.sparta.project.model.Bowling;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import com.sparta.project.repository.BowlingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BowlingService {

    private final BowlingRepository bowlingRepository;
    private final MatchService matchService;

    public Long inputMyScore(BowlingDto bowlingDto) {

        Match match = matchService.validate(bowlingDto.getMatch_id());
        User user = matchService.currentLoginUser();

        if(!bowlingRepository.existsByUserAndMatch(user, match) && bowlingDto.getMyScore() <= 300 && bowlingDto.getMyScore() >= 0) {
            bowlingRepository.save(Bowling.builder()
                    .myScore(bowlingDto.getMyScore())
                    .user(user)
                    .match(match)
                    .build());

            return calculateAverageScore(user);
        }
        return 0L;
    }

    public Long calculateAverageScore(User user) {
        long sum = 0;
        List<Bowling> bowling = bowlingRepository.findAllByUser(user);

        for (Bowling value : bowling) {
            sum += value.getMyScore();
        }

        if(bowling.size() != 0) {
            return sum / bowling.size();
        }
        return 0L;
    }

}
