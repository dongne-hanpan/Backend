package com.sparta.project.service;

import com.sparta.project.dto.MatchDto;
import com.sparta.project.model.InvitedUser;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import com.sparta.project.repository.InvitedUserRepository;
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
    private final InvitedUserRepository invitedUserRepository;
    private final UserRepository userRepository;

    //게시글 작성
    public void createMatch(MatchDto matchDto) {
        Match match = new Match(matchDto);
        matchDto.setWriter(currentLoginUser().getNickname());
        matchRepository.save(match);

        InvitedUser invitedUser = new InvitedUser();
        invitedUser.setUser(currentLoginUser());
        invitedUser.setMatch(match);

        invitedUserRepository.save(invitedUser);
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
    public List<InvitedUser> enterMatch(Long match_id) {

        Match match = validate(match_id);

        InvitedUser invitedUser = new InvitedUser();
        invitedUser.setUser(currentLoginUser());
        invitedUser.setMatch(match);

        if(invitedUserRepository.existsByMatchAndUser(match, currentLoginUser())) {
            return null;
        }
        invitedUserRepository.save(invitedUser);

        return null;
    }

    @Transactional
    public void deleteMatch_Host(Long match_id) {
        Match match = validate(match_id);
        String writer = match.getWriter();

        if(writer.equals(currentLoginUser().getNickname())) {
            matchRepository.deleteById(match_id);
        }else {
            InvitedUser invitedUser = invitedUserRepository.findByMatchAndUser(match, currentLoginUser());
            invitedUserRepository.delete(invitedUser);
        }
    }

    public Match validate(Long match_id) {
        Match match = matchRepository.findById(match_id).orElseThrow(() ->
                new IllegalArgumentException("게시물이 존재하지 않습니다"));

        if(!invitedUserRepository.existsByMatchAndUser(match, currentLoginUser())) {
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
