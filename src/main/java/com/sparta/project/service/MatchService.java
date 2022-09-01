package com.sparta.project.service;

import com.sparta.project.dto.MatchDto;
import com.sparta.project.model.InvitedUser;
import com.sparta.project.model.Match;
import com.sparta.project.repository.InvitedUserRepository;
import com.sparta.project.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final InvitedUserRepository invitedUserRepository;

    //게시글 작성
    public void createMatch(MatchDto matchDto) {
        Match match = new Match(matchDto);
        // 현재 로그인된 아이디 토큰 받아와 invitedUser엔티티에 save하는 작업 필요( 방장 )
        matchRepository.save(match);
    }

    //게시글 수정
    @Transactional
    public void updateMatch(Long matchId, MatchDto matchDto) {
        Match match = matchRepository.findById(matchId).orElseThrow(
                () -> new IllegalArgumentException("게시물이 없습니다.")
        );
        match.updateMatch(matchDto);
    }

    //match 입장 신청
    public List<InvitedUser> enterMatch(Long match_id) {
        // 현재 로그인된 아이디 토큰 받아와 invitedUser엔티티에 save하는 작업 필요
        return invitedUserRepository.findAllByMatchId(match_id);
    }

    public void deleteMatch_Host(Long match_id) {
        //호스트인지 검증하는 내용 필요
        matchRepository.deleteById(match_id);
    }

    @Transactional
    public void deleteMatch_NotHost(Long match_id) {
        //시큐리티컨텍스트홀더.getName() 하여 user_id 받아오기
        Long user_id = 1L;
        invitedUserRepository.deleteByMatch_IdAndUser_Id(user_id, match_id);
    }

    public Match validate(Long match_id) {
        Match match = matchRepository.findById(match_id).orElseThrow(() ->
                new IllegalArgumentException("게시물이 존재하지 않습니다"));

//        if(!invitedUserRepository.existsByMatchAndUser(match, user)) {
//            throw new IllegalArgumentException("초대되지 않은 방입니다");
//        }

        return match;
    }



}
