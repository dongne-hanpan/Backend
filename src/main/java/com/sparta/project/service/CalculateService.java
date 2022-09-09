package com.sparta.project.service;

import com.sparta.project.model.Bowling;
import com.sparta.project.model.Evaluation;
import com.sparta.project.model.User;
import com.sparta.project.repository.BowlingRepository;
import com.sparta.project.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculateService {

    private final EvaluationRepository evaluationRepository;
    private final BowlingRepository bowlingRepository;

    public double calculateMannerPoint(User user) {

        double sum = 0;
        double mannerPointAverage = 0;

        for (Evaluation evaluation : evaluationRepository.findAllByNickname(user.getNickname())) {
            sum += evaluation.getMannerPoint();
        }
        if(evaluationRepository.findAllByNickname(user.getNickname()).size() != 0 ) {
            mannerPointAverage = sum / evaluationRepository.findAllByNickname(user.getNickname()).size();
        }

        return mannerPointAverage;
    }

    public Long calculateAverageScore(User user) {
        long sum = 0;
        List<Bowling> bowling = bowlingRepository.findAllByUser(user);

        for (Bowling value : bowling) {
            sum += value.getMyScore();
        }
        if (bowling.size() != 0) {
            return sum / bowling.size();
        }
        return 0L;
    }

}
