package com.sparta.project.service;

import com.sparta.project.dto.EvaluationDto;
import com.sparta.project.dto.ResponseDto;
import com.sparta.project.dto.UserResponseDto;
import com.sparta.project.model.*;
import com.sparta.project.repository.BowlingRepository;
import com.sparta.project.repository.EvaluationRepository;
import com.sparta.project.repository.UserLisInMatchRepository;
import com.sparta.project.repository.UserRepository;
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
    private final MatchService matchService;
    private final BowlingRepository bowlingRepository;

    @Transactional(readOnly = true)
    public UserResponseDto getUserInfo(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponseDto::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다."));
    }

    // 현재 SecurityContext 에 있는 유저 정보 가져오기
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo() {
        return userRepository.findById(SecurityUtil.getCurrentUserId())
                .map(UserResponseDto::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
    }

    public void evaluateUser(EvaluationDto evaluationDto) {
        int count = 0;
        List<UserListInMatch> list = userLisInMatchRepository.findAllByMatchId(evaluationDto.getMatch_id());
        for (UserListInMatch userListInMatch : list) {
            if (userListInMatch.getUser().getNickname().equals(evaluationDto.getNickname()) || userListInMatch.getUser().getUsername().equals(getMyInfo().getUsername())) {
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

    public ResponseDto myPage(String sports) {
        double sum = 0;
        User user = matchService.currentLoginUser();
        ResponseDto responseDto = new ResponseDto();
        long totalAverage = 0;

        List<String> comment = new ArrayList<>();

        // 매너포인트 계산
        for (Evaluation evaluation : evaluationRepository.findAllByNickname(user.getNickname())) {
            sum = sum + evaluation.getMannerPoint();
            comment.add(evaluation.getComment());
        }
        double mannerPointAverage = sum / (double) evaluationRepository.findAllByNickname(user.getNickname()).size();

        // 마이페이지중 볼링페이지
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

            responseDto.setNickname(user.getNickname());
            responseDto.setScore(totalAverage);
            responseDto.setMannerPoint(mannerPointAverage);
            responseDto.setMatchCount(bowling.size());
            responseDto.setMatchList(list);
            responseDto.setComment(comment);
        }
        return responseDto;
    }

}