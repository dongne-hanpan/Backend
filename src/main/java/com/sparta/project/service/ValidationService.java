package com.sparta.project.service;

import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.repository.UserListInMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ValidationService {

    private final MatchRepository matchRepository;
    private final UserListInMatchRepository userListInMatchRepository;
    private final AuthService authService;

    public Match validate(Long match_id, String token) {

        User user = authService.getUserByToken(token);

        Match match = matchRepository.findById(match_id).orElseThrow(() ->
                new IllegalArgumentException("게시물이 존재하지 않습니다"));

        if (!userListInMatchRepository.existsByMatchAndUser(match, user)) {
            throw new IllegalArgumentException("초대되지 않은 방입니다");
        }
        return match;
    }

}
