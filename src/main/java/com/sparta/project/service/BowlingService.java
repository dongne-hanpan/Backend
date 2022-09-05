package com.sparta.project.service;

import com.sparta.project.dto.BowlingDto;
import com.sparta.project.model.Bowling;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import com.sparta.project.repository.BowlingRepository;
import com.sparta.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BowlingService {

    private final BowlingRepository bowlingRepository;
    private final MatchService matchService;

    private final UserRepository userRepository;

    public Long inputMyScore(BowlingDto bowlingDto) {

        long sum = 0;

        Match match = matchService.validate(bowlingDto.getMatch_id());
        User user = matchService.currentLoginUser();

        if(!bowlingRepository.existsByUserAndMatch(user, match) && bowlingDto.getMyScore() <= 300 && bowlingDto.getMyScore() >= 0) {
            bowlingRepository.save(Bowling.builder()
                    .myScore(bowlingDto.getMyScore())
                    .user(user)
                    .match(match)
                    .build());

            List<Bowling> bowling = bowlingRepository.findAllByUser(user);

            for (Bowling value : bowling) {
                sum = sum + value.getMyScore();
            }

            return sum / bowling.size();
        }
        return 0L;
    }

}
