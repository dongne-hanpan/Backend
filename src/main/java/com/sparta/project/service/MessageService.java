package com.sparta.project.service;

import com.sparta.project.dto.message.ChatMessageDto;
import com.sparta.project.model.Match;
import com.sparta.project.model.Message;
import com.sparta.project.model.User;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.repository.MessageRepository;
import com.sparta.project.repository.UserListInMatchRepository;
import com.sparta.project.repository.UserRepository;
import com.sparta.project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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

        Match match = matchRepository.findById(match_id).orElseThrow(()->
                new IllegalArgumentException("존재하지 않는 Match"));

        if (!userListInMatchRepository.existsByMatchAndUser(match, user)) {
            throw new IllegalArgumentException("초대가 되지않은 채팅방");
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
            throw new IllegalArgumentException("초대가 되지않은 채팅방");
        }

        List<Message> message = messageRepository.findAllByMatchOrderByCreatedAt(match);
        List<ChatMessageDto> messageList = new ArrayList<>();

        for (Message value : message) {
            messageList.add(ChatMessageDto.builder()
                    .message(value.getMessage())
                    .match_id(match_id)
                    .sender(value.getUser().getNickname())
                    .build()
            );
        }

    return messageList;
    }

}
