package com.sparta.project.service;

import com.sparta.project.dto.message.Notification;
import com.sparta.project.entity.Bowling;
import com.sparta.project.entity.Match;
import com.sparta.project.entity.User;
import com.sparta.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
@Service
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final UserRepository userRepository;
    private final CalculateService calculateService;
    private final BowlingRepository bowlingRepository;

    public SseEmitter subscribe(Long userId, String lastEventId) {

        String emitterId  = userId + "_" + System.currentTimeMillis();

        SseEmitter emitter;

        if (emitterRepository.findAllEmitterStartWithById(userId.toString()) != null){
            emitterRepository.deleteAllEmitterStartWithId(userId.toString());
            emitter = emitterRepository.save(emitterId, new SseEmitter(Long.MAX_VALUE)); //emitterId = key, SseEmitter = value
        }
        else {
            emitter = emitterRepository.save(emitterId, new SseEmitter(Long.MAX_VALUE));
        }

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId)); //네트워크 오류
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId)); //시간 초과
        emitter.onError((e) -> emitterRepository.deleteById(emitterId)); //오류

        String eventId = userId + "_" + System.currentTimeMillis();
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithById(userId.toString());
            eventCaches.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
        }

        return emitter;
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event().id(eventId).name("connect").data(data, MediaType.APPLICATION_JSON));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
        }
    }

    public void send(String receiver, String type, User user, Match match) {

        Notification notification = createNotification(receiver, type, user, match);
        Long userId = userRepository.findByNickname(receiver).getId();

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithById(userId.toString());

        sseEmitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, notification);
                }
        );
    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {

        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("connect")
                    .data(data, MediaType.APPLICATION_JSON));

//                    .reconnectTime(0));
//            emitter.complete();
//            emitterRepository.deleteById(id);

        } catch (Exception exception) {
            emitterRepository.deleteById(id);
            throw new IllegalArgumentException("Connection Error");
        }
    }

    private Notification createNotification(String receiver, String type, User user, Match match) {

        if (type.equals("apply")){
            List<Bowling> bowling = bowlingRepository.findAllByUser(user);

            return Notification.builder()
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
        }

        else if (type.equals("permit")) {
            return Notification.builder()
                    .receiver(receiver)
                    .alarmType(type)
                    .isRead(false)
                    .match_id(match.getId())
                    .match_date(match.getDate())
                    .sender(user.getNickname())
                    .sender_ProfileImage(user.getProfileImage())
                    .build();
        }

        else if (type.equals("deny")) {
            return Notification.builder()
                    .receiver(receiver)
                    .alarmType(type)
                    .isRead(false)
                    .match_id(match.getId())
                    .build();
        }

        else {
            return null;
        }
    }
}