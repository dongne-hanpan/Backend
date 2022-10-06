package com.sparta.project.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.project.dto.match.MatchRequestDto;
import com.sparta.project.dto.match.UserListInMatchDto;
import com.sparta.project.dto.user.InviteRequestDto;
import com.sparta.project.dto.user.InviteResponseDto;
import com.sparta.project.dto.match.MatchResponseDto;
import com.sparta.project.entity.*;
import com.sparta.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserListInMatchRepository userListInMatchRepository;
    private final UserRepository userRepository;
    private final CalculateService calculateService;
    private final BowlingRepository bowlingRepository;
    private final RequestUserListRepository requestUserListRepository;
    private final ValidationService validationService;
    private final AuthService authService;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;

    //게시글 작성
    public List<MatchResponseDto> createMatch(MatchRequestDto matchRequestDto, String token) {

        System.out.println(token);
        User user = authService.getUserByToken(token);

        matchRequestDto.setWriter(user.getNickname());
        matchRequestDto.setMatchStatus("recruit");

        Match match = new Match(matchRequestDto);
        matchRepository.save(match);

        userListInMatchRepository.save(UserListInMatch.builder()
                .user(user)
                .match(match)
                .build());

        return getMatchList(matchRequestDto.getRegion(), matchRequestDto.getSports());
    }

    @Transactional
    public void updateMatch(Long match_id, MatchRequestDto matchRequestDto, String token) {

        Match match = validationService.validate(match_id, token);

        User user = authService.getUserByToken(token);

        if (match.getWriter().equals(user.getNickname())) {
            match.updateMatch(matchRequestDto);
        }
    }

    public InviteResponseDto enterRequest(Long match_id, String token) {

        User user = authService.getUserByToken(token);
        Match match = matchRepository.findById(match_id).orElseThrow(() ->
                new NotFoundException("매치가 존재하지 않습니다."));

        if (Objects.equals(match.getMatchIntakeFull(), userListInMatchRepository.countByMatch(match))) {
            throw new IllegalArgumentException("참여 가능 인원이 초과되었습니다");
        }

        if (requestUserListRepository.existsByNicknameAndMatch(user.getNickname(), match)) {
            throw new IllegalArgumentException("이미 신청한 매치 입니다");
        }

        if (userListInMatchRepository.existsByMatchAndUser(match, user)) {
            throw new IllegalArgumentException("이미 소속된 매치입니다");
        }

        List<Bowling> bowling = bowlingRepository.findAllByUser(user);

        InviteResponseDto inviteResponseDto = InviteResponseDto.builder()
                .averageScore(calculateService.calculateAverageScore(user))
                .mannerPoint(calculateService.calculateMannerPoint(user))
                .match_id(match_id)
                .matchCount(bowling.size())
                .nickname(user.getNickname())
                .userLevel(calculateService.calculateLevel(user))
                .profileImage(user.getProfileImage())
                .build();

        RequestUserList requestUserList = new RequestUserList(inviteResponseDto, match);
        requestUserListRepository.save(requestUserList);

        notificationService.send(match.getWriter(), "apply", user, match);

        return inviteResponseDto;
    }

    @Transactional
    public void permitUser(InviteRequestDto inviteRequestDto, String token) {
        Match match = matchRepository.findById(inviteRequestDto.getMatch_id()).orElseThrow(() ->
                new NotFoundException("매치가 존재하지 않습니다."));

        User user = authService.getUserByToken(token);

        if (!user.getNickname().equals(match.getWriter())) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        user = userRepository.findByNickname(inviteRequestDto.getNickname());

        RequestUserList requestUserList = requestUserListRepository.findByNicknameAndMatch(user.getNickname(), match);

        if (inviteRequestDto.isPermit() && !userListInMatchRepository.existsByMatchAndUser(match, user)) {
            userListInMatchRepository.save(UserListInMatch.builder()
                    .match(match)
                    .user(user)
                    .build());

            notificationService.send(inviteRequestDto.getNickname(), "permit", authService.getUserByToken(token), match);
            requestUserListRepository.delete(requestUserList);

            messageRepository.save(Message.builder()
                    .message("님이 입장하셨습니다.")
                    .match(match)
                    .user(user)
                    .type("enter")
                    .build());

        } else if (!userListInMatchRepository.existsByMatchAndUser(match, user)) {
            notificationService.send(inviteRequestDto.getNickname(), "deny", authService.getUserByToken(token), match);
            requestUserListRepository.delete(requestUserList);

        } else {
            throw new IllegalArgumentException("이미 참여중인 회원입니다.");
        }
    }

    public void showRequestUserList(String token) {

        User user = authService.getUserByToken(token);

        notificationService.showRequestUser(user);

    }

    @Transactional
    public void deleteMatch(Long match_id, String token) {
        Match match = validationService.validate(match_id, token);
        String writer = match.getWriter();
        User user = authService.getUserByToken(token);

        if (writer.equals(user.getNickname())) {
            messageRepository.deleteByMatch(match); // 자식 객체를 먼저 삭제하기 위해 추가
            matchRepository.delete(match);
        } else {
            UserListInMatch userListInMatch = userListInMatchRepository.findByMatchAndUser(match, user);
            if (!bowlingRepository.existsByUserAndMatchId(user, match.getId()) && !match.getMatchStatus().equals("recruit")) {
                throw new IllegalArgumentException("결과 입력이 되지 않았습니다.");
            }
            userListInMatchRepository.delete(userListInMatch);

            messageRepository.save(Message.builder()
                    .message("님이 퇴장했습니다.")
                    .match(match)
                    .user(user)
                    .type("leave")
                    .build());

        }
    }

    public List<MatchResponseDto> getMatchList(Long region, String sports) {
        List<Match> matches = matchRepository.findAllByRegionAndSports(region, sports);
        return getMatchResponseDto(matches);
    }

    public List<MatchResponseDto> getMatchListAll(String sports) {
        List<Match> matches = matchRepository.findAllBySports(sports);
        return getMatchResponseDto(matches);
    }

    @Transactional
    public void setMatchStatusDone(Long match_id) {
        Match match = matchRepository.findById(match_id).orElseThrow(() -> new NotFoundException("매치가 존재하지 않습니다."));
        MatchRequestDto matchRequestDto = new MatchRequestDto();
        matchRequestDto.setMatchStatus("done");

        messageRepository.save(Message.builder()
                .message("경기가 종료되었습니다.")
                .match(match)
                .user(userRepository.findByNickname(match.getWriter()))
                .type("done")
                .build());

        match.changeStatus(matchRequestDto);
    }

    @Transactional
    public String setMatchStatusReserved(Long match_id, String token) {
        Match match = matchRepository.findById(match_id).orElseThrow(() -> new NotFoundException("매치가 존재하지 않습니다."));
        User user = authService.getUserByToken(token);

        if (user.getNickname().equals(match.getWriter())) {
            MatchRequestDto matchRequestDto = new MatchRequestDto();
            matchRequestDto.setMatchStatus("reserved");
            match.changeStatus(matchRequestDto);

            messageRepository.save(Message.builder()
                    .message("모집이 완료되었습니다.")
                    .match(match)
                    .user(user)
                    .type("reserved")
                    .build());

            return "모집이 마감되었습니다.";
        } else {
            return "권한이 없습니다";
        }
    }

    public MatchResponseDto chatRoomResponse(Long match_id, String token) {

        Match match = matchRepository.findById(match_id).orElseThrow(() -> new NotFoundException("매치가 존재하지 않습니다."));
        User user = authService.getUserByToken(token);

        if (!userListInMatchRepository.existsByMatchAndUser(match, user)) {
            throw new IllegalArgumentException("초대되지 않은 매치입니다.");
        }

        List<UserListInMatchDto> list = new ArrayList<>();

        for (UserListInMatch userListInMatch : userListInMatchRepository.findAllByMatchId(match_id)) {
            list.add(UserListInMatchDto.builder()
                    .nickname(userListInMatch.getUser().getNickname())
                    .profileImage(userListInMatch.getUser().getProfileImage())
                    .build());
        }

        return MatchResponseDto.builder()
                .match_id(match_id)
                .matchStatus(match.getMatchStatus())
                .date(match.getDate())
                .time(match.getTime())
                .place(match.getPlace())
                .writer(match.getWriter())
                .profileImage_HOST(userRepository.findByNickname(match.getWriter()).getProfileImage())
                .matchIntakeFull(match.getMatchIntakeFull())
                .matchIntakeCnt(userListInMatchRepository.countByMatch(match))
                .userListInMatch(list)
                .build();

    }

    @Transactional
    public String cancelMatch(Long match_id, String token) {

        User user = authService.getUserByToken(token);
        Match match = matchRepository.findById(match_id).orElseThrow(() -> new NotFoundException("매치가 존재하지 않습니다."));

        RequestUserList requestUserList = requestUserListRepository.findByNicknameAndMatch(user.getNickname(), match);
        requestUserListRepository.delete(requestUserList);

        return "신청이 취소되었습니다.";

    }

    private List<MatchResponseDto> getMatchResponseDto(List<Match> matches) {

        List<MatchResponseDto> matchResponseDto = new ArrayList<>();

        for (Match match : matches) {

            List<UserListInMatchDto> userList = new ArrayList<>();

            for (UserListInMatch userListInMatch : userListInMatchRepository.findAllByMatchId(match.getId())) {
                userList.add(UserListInMatchDto.builder()
                        .nickname(userListInMatch.getUser().getNickname())
                        .build());
            }

            if (match.getMatchStatus().equals("recruit")) {
                matchResponseDto.add(MatchResponseDto.builder()
                        .match_id(match.getId())
                        .writer(match.getWriter())
                        .region(match.getRegion())
                        .contents(match.getContents())
                        .date(match.getDate())
                        .time(match.getTime())
                        .place(match.getPlace())
                        .placeDetail(match.getPlaceDetail())
                        .sports(match.getSports())
                        .matchStatus(match.getMatchStatus())
                        .profileImage_HOST(userRepository.findByNickname(match.getWriter()).getProfileImage())
                        .matchCnt_HOST(bowlingRepository.findAllByUser(userRepository.findByNickname(match.getWriter())).size())
                        .averageScore_HOST(calculateService.calculateAverageScore(userRepository.findByNickname(match.getWriter())))
                        .mannerPoint_HOST(calculateService.calculateMannerPoint(userRepository.findByNickname(match.getWriter())))
                        .matchIntakeFull(match.getMatchIntakeFull())
                        .matchIntakeCnt(userListInMatchRepository.countByMatch(match))
                        .level_HOST(calculateService.calculateLevel(userRepository.findByNickname(match.getWriter())))
                        .userListInMatch(userList)
                        .build());
            }
        }
        return matchResponseDto;
    }

    public List<MatchResponseDto> reservedMatch(String token) {

        User user = authService.getUserByToken(token);

        List<UserListInMatch> matches = userListInMatchRepository.findAllByUser(user);
        List<MatchResponseDto> matchResponseDto = new ArrayList<>();

        for (UserListInMatch match : matches) {
            if (match.getMatch().getMatchStatus().equals("reserved")) {
                matchResponseDto.add(MatchResponseDto.builder()
                        .match_id(match.getMatch().getId())
                        .date(match.getMatch().getDate())
                        .place(match.getMatch().getPlace())
                        .placeDetail(match.getMatch().getPlaceDetail())
                        .time(match.getMatch().getTime())
                        .writer(match.getMatch().getWriter())
                        .profileImage_HOST(userRepository.findByNickname(match.getMatch().getWriter()).getProfileImage())
                        .mannerPoint_HOST(calculateService.calculateMannerPoint(userRepository.findByNickname(match.getMatch().getWriter())))
                        .averageScore_HOST(calculateService.calculateAverageScore(userRepository.findByNickname(match.getMatch().getWriter())))
                        .matchCnt_HOST(bowlingRepository.findAllByUser(userRepository.findByNickname(match.getMatch().getWriter())).size())
                        .level_HOST(calculateService.calculateLevel(userRepository.findByNickname(match.getMatch().getWriter())))
                        .build());
            }
        }
        return matchResponseDto;
    }

}
