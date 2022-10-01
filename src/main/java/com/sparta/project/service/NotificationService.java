package com.sparta.project.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.project.dto.user.InviteRequestReturnMessageDto;
import com.sparta.project.dto.user.InviteResponseDto;
import com.sparta.project.entity.Bowling;
import com.sparta.project.entity.Match;
import com.sparta.project.entity.RequestUserList;
import com.sparta.project.entity.User;
import com.sparta.project.repository.BowlingRepository;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.repository.RequestUserListRepository;
import com.sparta.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.sparta.project.controller.NotificationController.sseEmitters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final BowlingRepository bowlingRepository;
    private final CalculateService calculateService;
    private final RequestUserListRepository requestUserListRepository;

    public SseEmitter subscribe(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        List<Match> matches = matchRepository.findAllByWriter(user.getNickname());
        List<InviteResponseDto> userList = new ArrayList<>();

        for (Match match : matches) {
            List<RequestUserList> lists = requestUserListRepository.findAllByMatch(match);
            for (RequestUserList requestUserList : lists) {
                userList.add(InviteResponseDto.builder()
                        .match_id(match.getId())
                        .nickname(requestUserList.getNickname())
                        .userLevel(calculateService.calculateLevel(user))
                        .mannerPoint(calculateService.calculateMannerPoint(user))
                        .profileImage(user.getProfileImage())
                        .build());
            }
        }

        // 현재 클라이언트를 위한 SseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // 연결!!
            sseEmitter.send(SseEmitter.event().name("connect").data(userList));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user의 pk값을 key값으로 해서 SseEmitter를 저장
        sseEmitters.put(user.getId(), sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitters.remove(user.getId()));
        sseEmitter.onTimeout(() -> sseEmitters.remove(user.getId()));
        sseEmitter.onError((e) -> sseEmitters.remove(user.getId()));

        return sseEmitter;
    }

    public void showRequestUser(Long match_id, Long currentUserId) {

        Match match = matchRepository.findById(match_id).orElseThrow(() -> new NotFoundException("게시물이 존재하지 않습니다."));
        User user = userRepository.findByNickname(match.getWriter());
        Long userId = user.getId();

        if (sseEmitters.containsKey(userId) && !Objects.equals(userId, currentUserId)) {
            SseEmitter sseEmitter = sseEmitters.get(userId);

            List<Bowling> bowling = bowlingRepository.findAllByUser(user);

            user = userRepository.findById(currentUserId).orElseThrow();
            InviteResponseDto inviteResponseDto = InviteResponseDto.builder()
                    .averageScore(calculateService.calculateAverageScore(user))
                    .mannerPoint(calculateService.calculateMannerPoint(user))
                    .match_id(match_id)
                    .matchCount(bowling.size())
                    .nickname(user.getNickname())
                    .userLevel(calculateService.calculateLevel(user))
                    .profileImage(user.getProfileImage())
                    .build();

            try {
                sseEmitter.send(SseEmitter.event().name("request").data(inviteResponseDto));
            } catch (Exception e) {
                sseEmitters.remove(user.getId());
            }
        }
    }

    public void answerRequest(User user, Match match, boolean permit) {
        Long userId = user.getId(); // 알림 받는 사람 (매치에 신청한 사람)

        if (sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = sseEmitters.get(userId);

            if (permit) {
                InviteRequestReturnMessageDto returnMessageDto = InviteRequestReturnMessageDto.builder()
                        .date(match.getDate())
                        .time(match.getTime())
                        .place(match.getPlace())
                        .hostNickname(match.getWriter())
                        .match_id(match.getId())
                        .profileImage(userRepository.findByNickname(match.getWriter()).getProfileImage())
                        .returnMessage("신청이 수락되었습니다.")
                        .build();

                try {
                    sseEmitter.send(SseEmitter.event().name("message").data(returnMessageDto));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                InviteRequestReturnMessageDto returnMessageDto = InviteRequestReturnMessageDto.builder()
                        .date(match.getDate())
                        .time(match.getTime())
                        .place(match.getPlace())
                        .hostNickname(match.getWriter())
                        .match_id(match.getId())
                        .profileImage(userRepository.findByNickname(match.getWriter()).getProfileImage())
                        .returnMessage("신청이 거절되었습니다.")
                        .build();

                try {
                    sseEmitter.send(SseEmitter.event().name("message").data(returnMessageDto));
                } catch (Exception e) {
                    sseEmitters.remove(userId);
                }

            }

        }
    }

    public void deleteAlarm(Match match) {

        User user = userRepository.findByNickname(match.getWriter());
        List<InviteResponseDto> userList = new ArrayList<>();

        List<RequestUserList> lists = requestUserListRepository.findAllByMatch(match);
        for (RequestUserList requestUserList : lists) {
            userList.add(InviteResponseDto.builder()
                    .match_id(match.getId())
                    .nickname(requestUserList.getNickname())
                    .userLevel(calculateService.calculateLevel(user))
                    .mannerPoint(calculateService.calculateMannerPoint(user))
                    .profileImage(user.getProfileImage())
                    .build());
        }

        if (sseEmitters.containsKey(user.getId())) {
            SseEmitter sseEmitter = sseEmitters.get(user.getId());
            try {
                sseEmitter.send(SseEmitter.event().name("connect").data(userList));
            } catch (Exception e) {
                sseEmitters.remove(user.getId());
            }
        }
    }
}