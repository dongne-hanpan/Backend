package com.sparta.project.service;

import antlr.Token;
import com.sparta.project.dto.InviteRequestDto;
import com.sparta.project.dto.InviteResponseDto;
import com.sparta.project.dto.MatchDto;
import com.sparta.project.model.*;
import com.sparta.project.repository.*;
import com.sparta.project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserLisInMatchRepository userLisInMatchRepository;
    private final UserRepository userRepository;
    private final CalculateService calculateService;
    private final BowlingRepository bowlingRepository;
    private final RequestUserListRepository requestUserListRepository;
    private final ValidationService validationService;
    private final AuthService authService;

    //게시글 작성
    public void createMatch(MatchDto matchDto, String token) {

        User user = authService.getUserIdByToken(token);

        matchDto.setWriter(user.getNickname());

        Match match = new Match(matchDto);
        matchRepository.save(match);

        //작성자 본인을 match에 포함되도록 저장
        UserListInMatch userListInMatch = new UserListInMatch();
        userListInMatch.setUser(user);
        userListInMatch.setMatch(match);
        userLisInMatchRepository.save(userListInMatch);
    }

    //게시글 수정
    @Transactional
    public void updateMatch(Long match_id, MatchDto matchDto, String token) {

        Match match = validationService.validate(match_id, token);

        User user = authService.getUserIdByToken(token);

        if (match.getWriter().equals(user.getNickname())) {
            match.updateMatch(matchDto);
        }
    }

    //match 입장 신청
    public InviteResponseDto enterMatch(Long match_id, String token) {

        User user = authService.getUserIdByToken(token);

        Match match = matchRepository.findById(match_id).orElseThrow();

        List<Bowling> bowling = bowlingRepository.findAllByUser(user);

        InviteResponseDto inviteResponseDto = InviteResponseDto.builder()
                .averageScore(calculateService.calculateAverageScore(user))
                .mannerPoint(calculateService.calculateMannerPoint(user))
                .match_id(match_id)
                .matchCount(bowling.size())
                .nickname(user.getNickname())
                .build();

        RequestUserList requestUserList = new RequestUserList(inviteResponseDto);

        requestUserList.setMatch(match);
        requestUserListRepository.save(requestUserList);

        return inviteResponseDto;
    }

    //입장 수락 or 거절
    public void permitUser(InviteRequestDto inviteRequestDto, String token) {
        Match match = matchRepository.findById(inviteRequestDto.getMatch_id()).orElseThrow(() ->
                new IllegalArgumentException("매치가 존재하지 않습니다."));

        User user = authService.getUserIdByToken(token);

        if (!user.getNickname().equals(match.getWriter())) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        user = userRepository.findByNickname(inviteRequestDto.getNickname());
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

    public List<InviteResponseDto> showRequestUserList(String token) {

        User user = authService.getUserIdByToken(token);

        List<Match> list = matchRepository.findAllByWriter(user.getNickname());
        List<InviteResponseDto> userList = new ArrayList<>();

        if(list.size() != 0) {
            for (Match match : list) {
                for(int i=0; i<requestUserListRepository.findAllByMatch(match).size(); i++) {
                    user = userRepository.findByNickname(requestUserListRepository.findAllByMatch(match).get(i).getNickname());
                    userList.add(InviteResponseDto.builder()
                            .match_id(match.getId())
                            .nickname(requestUserListRepository.findAllByMatch(match).get(i).getNickname())
                            .matchCount(bowlingRepository.findAllByUser(user).size())
                            .averageScore(calculateService.calculateAverageScore(user))
                            .mannerPoint(calculateService.calculateMannerPoint(user))
                            .build()
                    );
                }

            }
        }else {
            return null;
        }
        return userList;

    }

    @Transactional
    public void deleteMatch(Long match_id, String token) {
        Match match = validationService.validate(match_id, token);
        String writer = match.getWriter();
        User user = authService.getUserIdByToken(token);

        if (writer.equals(user.getNickname())) {
            matchRepository.deleteById(match_id);
        } else {
            UserListInMatch userListInMatch = userLisInMatchRepository.findByMatchAndUser(match, user);
            userLisInMatchRepository.delete(userListInMatch);
        }
    }

}
