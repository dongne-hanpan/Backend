package com.sparta.project.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.project.dto.message.ChatMessageDto;
import com.sparta.project.entity.Match;
import com.sparta.project.entity.Message;
import com.sparta.project.entity.User;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.repository.MessageRepository;
import com.sparta.project.repository.UserListInMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MatchRepository matchRepository;
    private final UserListInMatchRepository userListInMatchRepository;
    private final MessageRepository messageRepository;
    private final AuthService authService;

    public void messageSender(ChatMessageDto chatMessageDto, Long match_id, String token) {

        User user = authService.getUserByToken(token);

        chatMessageDto.setSender(user.getNickname());
        chatMessageDto.setMatch_id(match_id);

        Match match = matchRepository.findById(match_id).orElseThrow(() ->
                new NotFoundException("매치가 존재하지 않습니다."));

        if (!userListInMatchRepository.existsByMatchAndUser(match, user)) {
            throw new IllegalArgumentException("초대 되지않은 매치입니다.");
        }

        messageRepository.save(Message.builder()
                .message(chatMessageDto.getMessage())
                .user(user)
                .match(match)
                .build());
    }

    public List<ChatMessageDto> showMessage(Long match_id, String token) {

        Match match = matchRepository.findById(match_id).orElseThrow();
        User user = authService.getUserByToken(token);

        if (!userListInMatchRepository.existsByMatchAndUser(match, user)) {
            throw new IllegalArgumentException("초대 되지않은 매치입니다.");
        }

        List<Message> messages = messageRepository.findAllByMatchOrderByCreatedAt(match);
        List<ChatMessageDto> messageList = new ArrayList<>();

        for (Message message : messages) {
            messageList.add(ChatMessageDto.builder()
                    .message(message.getMessage())
                    .match_id(match_id)
                    .sender(message.getUser().getNickname())
                    .createdAt(message.getCreatedAt())
                    .build()
            );
        }

        return messageList;
    }

}
