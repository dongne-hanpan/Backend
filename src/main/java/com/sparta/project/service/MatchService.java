package com.sparta.project.service;

import com.sparta.project.dto.match.MatchRequestDto;
import com.sparta.project.dto.user.InviteRequestDto;
import com.sparta.project.dto.user.InviteResponseDto;
import com.sparta.project.dto.match.MatchResponseDto;
import com.sparta.project.model.*;
import com.sparta.project.repository.*;
import lombok.RequiredArgsConstructor;
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
    public void createMatch(MatchRequestDto matchRequestDto, String token) {

        User user = authService.getUserByToken(token);

        matchRequestDto.setWriter(user.getNickname());

        Match match = new Match(matchRequestDto);
        matchRepository.save(match);

        //작성자 본인을 match에 포함되도록 저장
        userLisInMatchRepository.save(UserListInMatch.builder()
                .user(user)
                .match(match)
                .build());
    }

    //게시글 수정
    @Transactional
    public void updateMatch(Long match_id, MatchRequestDto matchRequestDto, String token) {

        Match match = validationService.validate(match_id, token);

        User user = authService.getUserByToken(token);

        if (match.getWriter().equals(user.getNickname())) {
            match.updateMatch(matchRequestDto);
        }
    }

    //match 입장 신청
    public InviteResponseDto enterRequestMatch(Long match_id, String token) {

        User user = authService.getUserByToken(token);

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

        User user = authService.getUserByToken(token);

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

        User user = authService.getUserByToken(token);

        List<Match> list = matchRepository.findAllByWriter(user.getNickname());
        List<InviteResponseDto> userList = new ArrayList<>();

        if (list.size() != 0) {
            for (Match match : list) {
                for (int i = 0; i < requestUserListRepository.findAllByMatch(match).size(); i++) {
                    user = userRepository.findByNickname(requestUserListRepository.findAllByMatch(match).get(i).getNickname());
                    userList.add(InviteResponseDto.builder()
                            .match_id(match.getId())
                            .nickname(requestUserListRepository.findAllByMatch(match).get(i).getNickname())
                            .userLevel(calculateService.calculateLevel(user))
                            .mannerPoint(calculateService.calculateMannerPoint(user))
                            .profileImage(user.getProfileImage())
                            .build()
                    );
                }

            }
        } else {
            return null;
        }
        return userList;

    }

    @Transactional
    public void deleteMatch(Long match_id, String token) {
        Match match = validationService.validate(match_id, token);
        String writer = match.getWriter();
        User user = authService.getUserByToken(token);

        if (writer.equals(user.getNickname())) {
            matchRepository.deleteById(match_id);
        } else {
            UserListInMatch userListInMatch = userLisInMatchRepository.findByMatchAndUser(match, user);
            userLisInMatchRepository.delete(userListInMatch);
        }
    }

    public List<MatchResponseDto> getMatchList(Long region, String sports) {
        List<Match> matches = matchRepository.findAllByRegionAndSports(region, sports);
        List<MatchResponseDto> list = new ArrayList<>();

        for (Match match : matches) {
            list.add(MatchResponseDto.builder()
                    .writer(match.getWriter())
                    .region(match.getRegion())
                    .contents(match.getContents())
                    .date(match.getDate())
                    .time(match.getTime())
                    .place(match.getPlace())
                    .sports(match.getSports())
                    .profileImage_HOST(null)
                    .mannerPoint_HOST(calculateService.calculateMannerPoint(userRepository.findByNickname(match.getWriter())))
                    .max_user(match.getMax_user())
                    .level_HOST(calculateService.calculateLevel(userRepository.findByNickname(match.getWriter())))
                    .build());
        }
        return list;

    }

}
