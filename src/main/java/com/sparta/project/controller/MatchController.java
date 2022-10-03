package com.sparta.project.controller;

import com.sparta.project.dto.match.MatchRequestDto;
import com.sparta.project.dto.user.InviteRequestDto;
import com.sparta.project.dto.user.InviteResponseDto;
import com.sparta.project.dto.match.MatchResponseDto;
import com.sparta.project.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @GetMapping("/list/{sports}")
    private List<MatchResponseDto> getMatchListAll(@PathVariable String sports) {
        return matchService.getMatchListAll(sports);
    }

    @PostMapping("/write")
    private List<MatchResponseDto> createMatch(@RequestBody MatchRequestDto matchRequestDto, @RequestHeader(value = "Authorization") String token) {
        return matchService.createMatch(matchRequestDto, token);
    }

    @PutMapping("/update/{match_id}")
    private MatchRequestDto updateMatch(@PathVariable Long match_id, @RequestBody MatchRequestDto matchRequestDto, @RequestHeader(value = "Authorization") String token) {
        matchService.updateMatch(match_id, matchRequestDto, token);
        return matchRequestDto;
    }

    @DeleteMapping("/delete/{match_id}")
    private void deleteMatch(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        matchService.deleteMatch(match_id, token);
    }

    @GetMapping(value = "/enter/{match_id}", consumes = MediaType.ALL_VALUE)
    private InviteResponseDto enterMatch(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return matchService.enterRequest(match_id, token);
    }

    @PostMapping(value = "/permit", consumes = MediaType.ALL_VALUE)
    private void permitUser(@RequestBody InviteRequestDto inviteRequestDto, @RequestHeader(value = "Authorization") String token) {
        matchService.permitUser(inviteRequestDto, token);
    }

    @GetMapping("/request")
    private void requestUserList(@RequestHeader(value = "Authorization") String token) {
        matchService.showRequestUserList(token);
    }

    @GetMapping("/match-status-reserved/{match_id}")
    private String setMatchStatusReserved(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return matchService.setMatchStatusReserved(match_id, token);
    }

    @GetMapping("/chatroom/{match_id}")
    private MatchResponseDto showChatRoomData(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return matchService.chatRoomResponse(match_id, token);
    }

    @GetMapping("/cancel/{match_id}")
    private String cancelMatch(@PathVariable Long match_id, @RequestHeader(value = "Authorization") String token) {
        return matchService.cancelMatch(match_id, token);
    }

    @GetMapping("/reserved-match")
        private List<MatchResponseDto> reservedMatch(@RequestHeader(value = "Authorization") String token) {
           return matchService.reservedMatch(token);
        }
}
