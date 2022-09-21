package com.sparta.project.controller;

import com.sparta.project.dto.match.MatchRequestDto;
import com.sparta.project.dto.user.InviteRequestDto;
import com.sparta.project.dto.user.InviteResponseDto;
import com.sparta.project.dto.match.MatchResponseDto;
import com.sparta.project.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/match")
@RestController
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/list/{region}/{sports}")
    private List<MatchResponseDto> getMatchList(@PathVariable Long region, @PathVariable String sports) {
       return matchService.getMatchList(region, sports);
    }

    @PostMapping("/write")
    private List<MatchResponseDto> createMatch(@RequestBody MatchRequestDto matchRequestDto, @RequestHeader(value = "Authorization") String token) {
        return matchService.createMatch(matchRequestDto, token);

    }

    @PutMapping("/update/{match_id}")
    private MatchRequestDto updateMatch(@PathVariable Long match_id, @RequestBody MatchRequestDto matchRequestDto, @RequestHeader(value = "Authorization") String token) {
        try {
            matchService.updateMatch(match_id, matchRequestDto, token);
            return matchRequestDto;
        } catch (Exception e) {
            return null;  // 수정 실패시 에러반환 필요
        }
    }

    @DeleteMapping("/delete/{match_id}")
    private void deleteMatch(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        matchService.deleteMatch(match_id, token);
    }

    //입장신청
    @GetMapping("/enter/{match_id}")
    private InviteResponseDto enterMatch(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return matchService.enterRequest(match_id, token);
    }

    //입장 수락 or 거절
    @PostMapping("/permit")
    private void permitUser(@RequestBody InviteRequestDto inviteRequestDto, @RequestHeader(value = "Authorization") String token) {
        matchService.permitUser(inviteRequestDto, token);
    }

    //신청 유저 목록 매치별로 보여주기
    @GetMapping("/request")
    private List<InviteResponseDto> requestUserList(@RequestHeader(value = "Authorization") String token) {
        return matchService.showRequestUserList(token);
    }

    @GetMapping("/matchstatus/{match_id}")
    private String setMatchStatus(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return matchService.setMatchStatus(match_id, token);
    }

    @GetMapping("/chatroom/{match_id}")
    private MatchResponseDto showChatRoomData(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return matchService.chatRoomResponse(match_id, token);
    }

    @GetMapping("/cancel/{match_id}")
    private String cancelMatch(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return matchService.cancelMatch(match_id, token);
    }

}
