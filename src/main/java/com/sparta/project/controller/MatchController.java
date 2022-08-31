package com.sparta.project.controller;

import com.sparta.project.dto.MatchDto;
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
        if(sports.equals("bowling")) {
            return matchRepository.findAllByRegion(region);
        }
        return null;
    }

    @PostMapping("/write")
    private void makeMatch(@RequestBody MatchDto matchDto) {
        matchDto.setWriter("test");
        matchService.createMatch(matchDto);
    }

    @PutMapping("/update/{match_id}")
    private void updateMatch(@PathVariable Long match_id, @RequestBody MatchDto matchDto){
        matchService.updateMatch(match_id, matchDto);
    }

    @DeleteMapping("/delete/{match_id}")
    private void deleteMatch(@PathVariable Long match_id) {
        matchRepository.deleteById(match_id);
    }

    //후기 입력

}
