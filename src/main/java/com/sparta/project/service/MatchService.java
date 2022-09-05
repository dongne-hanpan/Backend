package com.sparta.project.service;

import com.sparta.project.dto.MatchDto;
import com.sparta.project.model.UserListInMatch;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import com.sparta.project.repository.UserLisInMatchRepository;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserLisInMatchRepository userLisInMatchRepository;
    private final UserRepository userRepository;

    //게시글 작성
    public void createMatch(MatchDto matchDto) {
        Match match = new Match(matchDto);
        matchDto.setWriter(currentLoginUser().getNickname());
        matchRepository.save(match);

        UserListInMatch userListInMatch = new UserListInMatch();
        userListInMatch.setUser(currentLoginUser());
        userListInMatch.setMatch(match);

        userLisInMatchRepository.save(userListInMatch);
    }

    //게시글 수정
    @Transactional
    public void updateMatch(Long match_id, MatchDto matchDto) {
        Match match = validate(match_id);

        if(match.getWriter().equals(currentLoginUser().getNickname())) {
            match.updateMatch(matchDto);
        }
    }

    //match 입장 신청
    public List<UserListInMatch> enterMatch(Long match_id) {

        Match match = matchRepository.findById(match_id).orElseThrow(() ->
                new IllegalArgumentException("게시물이 존재하지 않습니다"));

        UserListInMatch userListInMatch = new UserListInMatch();
        userListInMatch.setUser(currentLoginUser());
        userListInMatch.setMatch(match);

        if(userLisInMatchRepository.existsByMatchAndUser(match, currentLoginUser())) {
            return null;
        }
        userLisInMatchRepository.save(userListInMatch);

        return null;
    }

    @Transactional
    public void deleteMatch_Host(Long match_id) {
        Match match = validate(match_id);
        String writer = match.getWriter();

        if(writer.equals(currentLoginUser().getNickname())) {
            matchRepository.deleteById(match_id);
        }else {
            UserListInMatch userListInMatch = userLisInMatchRepository.findByMatchAndUser(match, currentLoginUser());
            userLisInMatchRepository.delete(userListInMatch);
        }
    }

    public Match validate(Long match_id) {
        Match match = matchRepository.findById(match_id).orElseThrow(() ->
                new IllegalArgumentException("게시물이 존재하지 않습니다"));

        if(!userLisInMatchRepository.existsByMatchAndUser(match, currentLoginUser())) {
            throw new IllegalArgumentException("초대되지 않은 방입니다");
        }
        return match;
    }

    public User currentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return userRepository.findById(Long.parseLong(userId)).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원"));
    }
}
