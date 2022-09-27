package com.sparta.project.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.project.entity.Match;
import com.sparta.project.entity.User;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service

public class NotificationService {


    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    public void notifyEvent(Long match_id) {
        Match match = matchRepository.findById(match_id).orElseThrow(() -> new NotFoundException("게시물이 존재하지 않습니다."));
        User user = userRepository.findByNickname(match.getWriter());
        Long userId = user.getId();

        if(sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = sseEmitters.get(userId);

            try {
                sseEmitter.send(SseEmitter.event().name("event").data(""));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
