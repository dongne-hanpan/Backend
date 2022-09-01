package com.sparta.project.service;

import com.sparta.project.dto.AverageDto;
import com.sparta.project.model.Average;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import com.sparta.project.repository.AverageRepository;
import com.sparta.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AverageService {

    private final AverageRepository averageRepository;
    private final MatchService matchService;

    private final UserRepository userRepository;

    public Long inputMyScore(AverageDto averageDto) {

        long sum = 0;

        Match match = matchService.validate(averageDto.getMatch_id());
        User user = matchService.currentLoginUser();

        if(!averageRepository.existsByUserAndMatch(user, match) && averageDto.getMyScore() <= 300 && averageDto.getMyScore() >= 0) {
            averageRepository.save(Average.builder()
                    .myScore(averageDto.getMyScore())
                    .user(user)
                    .match(match)
                    .build());

            averageDto.setUser_id(user.getId());

            List<Average> average = averageRepository.findAllByUser(user);

            for (Average value : average) {
                sum = sum + value.getMyScore();
            }

            long totalAverage = sum / average.size();

            user.setTotalAverage(totalAverage);
            user.setMatchCount(average.size());

            userRepository.save(user);

            return totalAverage;
        }
        return null;
    }

}
