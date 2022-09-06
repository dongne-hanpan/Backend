package com.sparta.project.controller;

import com.sparta.project.dto.InviteRequestDto;
import com.sparta.project.dto.InviteResponseDto;
import com.sparta.project.dto.MatchDto;
import com.sparta.project.model.RequestUserList;
import com.sparta.project.model.User;
import com.sparta.project.model.UserListInMatch;
import com.sparta.project.model.Match;
import com.sparta.project.repository.MatchRepository;
import com.sparta.project.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/match")
@RestController
public class MatchController {

    private final MatchRepository matchRepository;
    private final MatchService matchService;

    @GetMapping("/list/{region}/{sports}")
    private List<Match> getMatchList(@PathVariable Long region, @PathVariable String sports) {
        if (sports.equals("bowling")) {
            return matchRepository.findAllByRegion(region);
        }
        return null;
    }

    @PostMapping("/write")
    private MatchDto createMatch(@RequestBody MatchDto matchDto) {
        matchDto.setWriter(matchService.currentLoginUser().getNickname());
        matchService.createMatch(matchDto);
        return matchDto;
    }

    @PutMapping("/update/{match_id}")
    private MatchDto updateMatch(@PathVariable Long match_id, @RequestBody MatchDto matchDto) {
        try {
            matchService.updateMatch(match_id, matchDto);
            return matchDto;
        } catch (Exception e) {
            return null;
        }
    }

    @DeleteMapping("/delete/{match_id}")
    private void deleteMatch_Host(@PathVariable Long match_id) {
        matchService.deleteMatch_Host(match_id);
    }

    //입장신청
    @GetMapping("/enter/{match_id}")
    private InviteResponseDto enterMatch(@PathVariable Long match_id) {
        return matchService.enterMatch(match_id);
    }

    //입장 수락 or 거절
    @PostMapping("/permit")
    private void permitUser(@RequestBody InviteRequestDto inviteRequestDto) {
        matchService.permitUser(inviteRequestDto);
    }

    //신청 유저 목록 매치별로 보여주기
    @GetMapping("/request")
    private List<InviteResponseDto> requestUserList() {
        return matchService.showRequestUserList();
    }
}
