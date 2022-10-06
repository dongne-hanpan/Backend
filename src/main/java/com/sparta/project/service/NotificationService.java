package com.sparta.project.service;

import com.sparta.project.dto.message.NotificationDto;
import com.sparta.project.dto.user.InviteResponseDto;
import com.sparta.project.entity.Bowling;
import com.sparta.project.entity.Match;
import com.sparta.project.entity.RequestUserList;
import com.sparta.project.entity.User;
import com.sparta.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import static com.sparta.project.controller.NotificationController.sseEmitters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Service
public class NotificationService {

    private final UserRepository userRepository;
    private final CalculateService calculateService;
    private final BowlingRepository bowlingRepository;
    private final MatchRepository matchRepository;
    private final RequestUserListRepository requestUserListRepository;

    public SseEmitter subscribe(Long userId) {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // 연결!!
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sseEmitters.put(userId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitters.remove(userId));
        sseEmitter.onTimeout(() -> sseEmitters.remove(userId));
        sseEmitter.onError((e) -> sseEmitters.remove(userId));

        return sseEmitter;
    }


    public void send(String receiver, String type, User user, Match match) {

        NotificationDto notificationDto = createNotification(receiver, type, user, match);

        Long receiverId = userRepository.findByNickname(receiver).getId();

        if (sseEmitters.containsKey(receiverId)) {
            SseEmitter sseEmitter = sseEmitters.get(receiverId);

            try {
                assert notificationDto != null;
                sseEmitter.send(SseEmitter.event().name("connect").data(notificationDto));
            } catch (Exception e) {
                sseEmitters.remove(receiverId);
            }
        }
    }

    public void showRequestUser(User receiver) {

        if (sseEmitters.containsKey(receiver.getId())) {
            SseEmitter sseEmitter = sseEmitters.get(receiver.getId());

            List<Match> list = matchRepository.findAllByWriter(receiver.getNickname());

            List<NotificationDto> userList = new ArrayList<>();

            if (list.size() != 0) {
                for (Match match : list) {
                    for (int i = 0; i < requestUserListRepository.findAllByMatch(match).size(); i++) {
                        User user = userRepository.findByNickname(requestUserListRepository.findAllByMatch(match).get(i).getNickname());

                        List<Bowling> bowling = bowlingRepository.findAllByUser(user);

                        userList.add(NotificationDto.builder()
                                .alarmType("apply")
                                .receiver(receiver.getNickname())
                                .match_id(match.getId())
                                .sender(user.getNickname())
                                .sender_ProfileImage(user.getProfileImage())
                                .sender_AverageScore(calculateService.calculateAverageScore(user))
                                .sender_Level(calculateService.calculateLevel(user))
                                .sender_MannerPoint(calculateService.calculateMannerPoint(user))
                                .sender_MatchCnt(bowling.size())
                                .build()
                        );
                    }
                }
            }

            try {
                sseEmitter.send(SseEmitter.event().name("connect").data(userList));
            } catch (Exception e) {
                sseEmitters.remove(receiver.getId());
            }

        }
    }


    private NotificationDto createNotification(String receiver, String type, User user, Match match) {

        if (type.equals("apply")) {
            List<Bowling> bowling = bowlingRepository.findAllByUser(user);

            return NotificationDto.builder()
                    .receiver(receiver)
                    .alarmType(type)
                    .isRead(false)
                    .match_id(match.getId())
                    .sender(user.getNickname())
                    .sender_ProfileImage(user.getProfileImage())
                    .sender_AverageScore(calculateService.calculateAverageScore(user))
                    .sender_Level(calculateService.calculateLevel(user))
                    .sender_MannerPoint(calculateService.calculateMannerPoint(user))
                    .sender_MatchCnt(bowling.size())
                    .build();

        } else if (type.equals("permit")) {
            return NotificationDto.builder()
                    .receiver(receiver)
                    .alarmType(type)
                    .isRead(false)
                    .match_id(match.getId())
                    .match_date(match.getDate())
                    .sender(user.getNickname())
                    .sender_ProfileImage(user.getProfileImage())
                    .build();

        } else if (type.equals("deny")) {
            return NotificationDto.builder()
                    .receiver(receiver)
                    .alarmType(type)
                    .isRead(false)
                    .match_id(match.getId())
                    .match_date(match.getDate())
                    .build();
        } else {
            return null;
        }
    }
}