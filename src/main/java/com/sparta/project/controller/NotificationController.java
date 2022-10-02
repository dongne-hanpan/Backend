package com.sparta.project.controller;

import com.sparta.project.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;
//    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/sub/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable Long userId, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId ) {
        return notificationService.subscribe(userId, lastEventId);
    }

}
//@RequestHeader(value = "Authorization") String token