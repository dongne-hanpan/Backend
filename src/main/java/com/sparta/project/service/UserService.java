package com.sparta.project.service;

import com.sparta.project.dto.match.MatchResponseDto;
import com.sparta.project.dto.user.EvaluationDto;
import com.sparta.project.dto.user.MyPageResponseDto;
import com.sparta.project.model.*;
import com.sparta.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserListInMatchRepository userListInMatchRepository;
    private final EvaluationRepository evaluationRepository;
    private final BowlingRepository bowlingRepository;
    private final CalculateService calculateService;
    private final AuthService authService;

    public void evaluateUser(EvaluationDto evaluationDto, String token) {

        int count = 0;

        User user = authService.getUserByToken(token);

        List<UserListInMatch> list = userListInMatchRepository.findAllByMatchId(evaluationDto.getMatch_id());
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
            List<MatchResponseDto> list = new ArrayList<>();
            for (UserListInMatch invitedUser : userListInMatchRepository.findAllByUser(user)) {
                if (invitedUser.getMatch().getSports().equals(sports))
                    list.add(MatchResponseDto.builder()
                            .match_id(invitedUser.getMatch().getId())
                            .matchIntakeCnt(userListInMatchRepository.countByMatch(invitedUser.getMatch()))
                            .matchIntakeFull(invitedUser.getMatch().getMatchIntakeFull())
                            .contents(invitedUser.getMatch().getContents())
                            .date(invitedUser.getMatch().getDate())
                            .time(invitedUser.getMatch().getTime())
                            .place(invitedUser.getMatch().getPlace())
                            .placeDetail(invitedUser.getMatch().getPlaceDetail())
                            .region(invitedUser.getMatch().getRegion())
                            .sports(invitedUser.getMatch().getSports())
                            .writer(invitedUser.getMatch().getWriter())
                            .level_HOST(calculateService.calculateLevel(user))
                            .matchStatus(invitedUser.getMatch().getMatchStatus())
                            .build());
            }
            List<Bowling> bowling = bowlingRepository.findAllByUser(user);
            for (Bowling value : bowling) {
                sumScore = sumScore + value.getMyScore();
            }

            if (bowling.size() != 0) {
                totalAverage = sumScore / bowling.size();
            }

            return MyPageResponseDto.builder()
                    .comment(comment)
                    .nickname(user.getNickname())
                    .mannerPoint(calculateService.calculateMannerPoint(user))
                    .matchCount(bowling.size())
                    .matchList(list)
                    .profileImage(user.getProfileImage())
                    .score(totalAverage)
                    .build();
        }
        return null;
    }


}