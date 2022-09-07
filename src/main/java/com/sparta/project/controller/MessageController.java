package com.sparta.project.controller;


import com.sparta.project.dto.ChatMessageDto;
import com.sparta.project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class MessageController {

    private final MessageService messageService;
    private final SimpMessageSendingOperations messagingTemplate;

    //메세지 보내기
    @MessageMapping("/chat/{match_id}")
    public void message(@RequestBody ChatMessageDto chatMessageDto, @DestinationVariable Long match_id, @Header(value = "Authorization") String token) {

        messageService.messageSender(chatMessageDto, match_id, token);

        messagingTemplate.convertAndSend("/queue/match/" + match_id, chatMessageDto);
    }

    @ResponseBody
    //메세지 채팅방에 뿌려주기
    @GetMapping("/chat/message/{match_id}")
    public List<ChatMessageDto> showMessage(@PathVariable Long match_id) {
        return messageService.showMessage(match_id);
    }
}
