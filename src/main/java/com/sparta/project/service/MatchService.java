package com.sparta.project.service;

import com.sparta.project.dto.MatchDto;
import com.sparta.project.model.Match;
import com.sparta.project.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public void createMatch(MatchDto matchDto) {
        Match match = new Match(matchDto);
        matchRepository.save(match);
    }

    @Transactional
    public void updateMatch(Long matchId, MatchDto matchDto) {
        Match match = matchRepository.findById(matchId).orElseThrow(
                () -> new IllegalArgumentException("게시물이 없습니다.")
        );
        match.updateMatch(matchDto);
    }


}
