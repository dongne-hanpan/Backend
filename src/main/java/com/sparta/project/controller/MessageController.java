package com.sparta.project.controller;


import com.sparta.project.dto.message.ChatMessageDto;
import com.sparta.project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class MessageController {

    private final MessageService messageService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/{match_id}")
    public void message(@RequestBody ChatMessageDto chatMessageDto, @DestinationVariable Long match_id, @Header(value = "Authorization") String token) {
        messageService.messageSender(chatMessageDto, match_id, token);
        messagingTemplate.convertAndSend("/queue/match/" + match_id, chatMessageDto);
    }

    @ResponseBody
    @GetMapping("/chat/message/{match_id}")
    public List<ChatMessageDto> showMessage(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return messageService.showMessage(match_id, token);
    }
}
