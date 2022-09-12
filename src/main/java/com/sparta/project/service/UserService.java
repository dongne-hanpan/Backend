package com.sparta.project.service;

import com.sparta.project.dto.user.EvaluationDto;
import com.sparta.project.dto.user.MyPageResponseDto;
import com.sparta.project.dto.user.UserResponseDto;
import com.sparta.project.model.*;
import com.sparta.project.repository.*;
import com.sparta.project.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserLisInMatchRepository userLisInMatchRepository;
    private final EvaluationRepository evaluationRepository;
    private final BowlingRepository bowlingRepository;
    private final CalculateService calculateService;
    private final AuthService authService;

    public void evaluateUser(EvaluationDto evaluationDto, String token) {

        int count = 0;

        User user = authService.getUserByToken(token);

        List<UserListInMatch> list = userLisInMatchRepository.findAllByMatchId(evaluationDto.getMatch_id());
        for (UserListInMatch userListInMatch : list) {
            if (userListInMatch.getUser().getNickname().equals(evaluationDto.getNickname()) || userListInMatch.getUser().getUsername().equals(user.getUsername())) {
                count++;
            }
        }
        if (count == 2) {
            evaluationRepository.save(Evaluation.builder()
                    .nickname(evaluationDto.getNickname())
                    .user(userRepository.findByNickname(evaluationDto.getNickname()))
                    .comment(evaluationDto.getComment())
                    .mannerPoint(evaluationDto.getMannerPoint())
                    .match_id(evaluationDto.getMatch_id())
                    .build());

        }
        count = 0;

    }

    public MyPageResponseDto myPage(String sports, String token) {
        User user = authService.getUserByToken(token);
        long totalAverage = 0;

        List<String> comment = new ArrayList<>();

        // 입력된 코멘트 불러오기
        for (Evaluation evaluation : evaluationRepository.findAllByNickname(user.getNickname())) {
            comment.add(evaluation.getComment());
        }

        // 마이페이지 - 볼링
        if (sports.equals("bowling")) {
            long sumScore = 0L;
            List<Match> list = new ArrayList<>();
            for (UserListInMatch invitedUser : userLisInMatchRepository.findAllByUser(user)) {
                if (invitedUser.getMatch().getSports().equals(sports)) list.add(invitedUser.getMatch());
            }

            List<Bowling> bowling = bowlingRepository.findAllByUser(user);
            for (Bowling value : bowling) { sumScore = sumScore + value.getMyScore(); }

            if(bowling.size() != 0) {
                totalAverage = sumScore / bowling.size();
            }

            return MyPageResponseDto.builder()
                    .comment(comment)
                    .nickname(user.getNickname())
                    .mannerPoint(calculateService.calculateMannerPoint(user))
                    .matchCount(bowling.size())
                    .matchList(list)
                    .profileImage("test")
                    .score(totalAverage)
                    .build();
        }
        return null;
    }



}