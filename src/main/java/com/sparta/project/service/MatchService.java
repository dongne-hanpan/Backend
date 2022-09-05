package com.sparta.project.service;

import com.sparta.project.dto.InviteRequestDto;
import com.sparta.project.dto.InviteResponseDto;
import com.sparta.project.dto.MatchDto;
import com.sparta.project.model.*;
import com.sparta.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserLisInMatchRepository userLisInMatchRepository;
    private final UserRepository userRepository;
    //private final BowlingService bowlingService;
    //private final UserService userService;
    private final EvaluationRepository evaluationRepository;
    private final BowlingRepository bowlingRepository;
    private final RequestUserListRepository requestUserListRepository;

    //게시글 작성
    public void createMatch(MatchDto matchDto) {

        matchDto.setWriter(currentLoginUser().getNickname());

        Match match = new Match(matchDto);
        matchRepository.save(match);

        //작성자 본인을 match에 포함되도록 저장
        UserListInMatch userListInMatch = new UserListInMatch();
        userListInMatch.setUser(currentLoginUser());
        userListInMatch.setMatch(match);
        userLisInMatchRepository.save(userListInMatch);
    }

    //게시글 수정
    @Transactional
    public void updateMatch(Long match_id, MatchDto matchDto) {
        Match match = validate(match_id);

        if (match.getWriter().equals(currentLoginUser().getNickname())) {
            match.updateMatch(matchDto);
        }
    }

    //match 입장 신청
    public InviteResponseDto enterMatch(Long match_id) {

        User user = currentLoginUser();

        InviteResponseDto inviteResponseDto = InviteResponseDto.builder()
                .averageScore(calculateAverageScore(user))
                .mannerPoint(calculateMannerPoint(user))
                .match_id(match_id)
                .nickname(user.getNickname())
                .build();

        Match match = matchRepository.findById(match_id).orElseThrow();

        RequestUserList requestUserList = new RequestUserList(inviteResponseDto);

        requestUserList.setMatch(match);
        requestUserListRepository.save(requestUserList);

        return inviteResponseDto;
    }

    //입장 수락 or 거절
    public void permitUser(InviteRequestDto inviteRequestDto) {
        Match match = matchRepository.findById(inviteRequestDto.getMatch_id()).orElseThrow(() ->
                new IllegalArgumentException("매치가 존재하지 않습니다."));

        String HOST = match.getWriter();

        if (!currentLoginUser().getNickname().equals(HOST)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }
        User user = userRepository.findByNickname(inviteRequestDto.getNickname());
        RequestUserList requestUserList = requestUserListRepository.findByNickname(user.getNickname());

        if (inviteRequestDto.isPermit()) {
            userLisInMatchRepository.save(UserListInMatch.builder()
                    .match(match)
                    .user(user)
                    .build());

            requestUserListRepository.delete(requestUserList);

        } else {
            requestUserListRepository.delete(requestUserList);
        }
    }


    @Transactional
    public void deleteMatch_Host(Long match_id) {
        Match match = validate(match_id);
        String writer = match.getWriter();

        if (writer.equals(currentLoginUser().getNickname())) {
            matchRepository.deleteById(match_id);
        } else {
            UserListInMatch userListInMatch = userLisInMatchRepository.findByMatchAndUser(match, currentLoginUser());
            userLisInMatchRepository.delete(userListInMatch);
        }
    }

    public Match validate(Long match_id) {
        Match match = matchRepository.findById(match_id).orElseThrow(() ->
                new IllegalArgumentException("게시물이 존재하지 않습니다"));

        if (!userLisInMatchRepository.existsByMatchAndUser(match, currentLoginUser())) {
            throw new IllegalArgumentException("초대되지 않은 방입니다");
        }
        return match;
    }

    public User currentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return userRepository.findById(Long.parseLong(userId)).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원"));
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
}
