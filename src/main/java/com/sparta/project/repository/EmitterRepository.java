package com.sparta.project.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter); //Emitter 저장

    void saveEventCache(String eventCacheId, Object event); //이벤트 저장

    Map<String, SseEmitter> findAllEmitterStartWithById(String Id); //해당 회원과 관련된 모든 Emitter를 찾는다

    Map<String, Object> findAllEventCacheStartWithById(String Id); //해당 회원과관련된 모든 이벤트를 찾는다

    void deleteById(String id); //Emitter를 지운다

    void deleteAllEmitterStartWithId(String Id); //해당 회원과 관련된 모든 Emitter를 지운다

    void deleteAllEventCacheStartWithId(String Id); //해당 회원과 관련된 모든 이벤트를 지운다
}
