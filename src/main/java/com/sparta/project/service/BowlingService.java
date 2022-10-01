package com.sparta.project.service;

import com.sparta.project.dto.sports.BowlingDto;
import com.sparta.project.entity.Bowling;
import com.sparta.project.entity.Match;
import com.sparta.project.entity.Message;
import com.sparta.project.entity.User;
import com.sparta.project.repository.BowlingRepository;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.repository.MessageRepository;
import com.sparta.project.repository.UserListInMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BowlingService {

    private final BowlingRepository bowlingRepository;
    private final ValidationService validationService;
    private final AuthService authService;
    private final CalculateService calculateService;
    private final UserListInMatchRepository userListInMatchRepository;
    private final MatchService matchService;
    private final MessageRepository messageRepository;

    public Long inputMyScore(BowlingDto bowlingDto, String token) {

        Match match = validationService.validate(bowlingDto.getMatch_id(), token);
        User user = authService.getUserByToken(token);

        if(match.getMatchStatus().equals("recruit")) {
            throw new IllegalArgumentException("모집이 종료되지 않았습니다.");
        }

        if(bowlingRepository.existsByUserAndMatch(user, match)) {
            throw new IllegalArgumentException("결과가 이미 등록되었습니다.");
        }

        if(bowlingDto.getMyScore() <= 300 && bowlingDto.getMyScore() >= 0) {
            bowlingRepository.save(Bowling.builder()
                    .myScore(bowlingDto.getMyScore())
                    .user(user)
                    .match(match)
                    .build());

            messageRepository.save(Message.builder()
                    .message("결과가 입력되었습니다.")
                    .match(match)
                    .user(user)
                    .type("result")
                    .build());

            long resultCnt = bowlingRepository.countByMatch(match);

            if(resultCnt == userListInMatchRepository.countByMatch(match)) {
                matchService.setMatchStatusDone(match.getId());
            }
            return calculateService.calculateAverageScore(user);
        }
        return 0L;
    }
}
