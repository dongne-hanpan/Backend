package com.sparta.project.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String eventCacheId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithById(String Id);

    Map<String, Object> findAllEventCacheStartWithId(String Id);

    void deleteById(String id);

    void deleteAllEmitterStartWithId(String Id);

    void deleteAllEventCacheStartWithId(String Id);
}
