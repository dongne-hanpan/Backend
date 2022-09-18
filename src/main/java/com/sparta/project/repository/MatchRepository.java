package com.sparta.project.repository;

import com.sparta.project.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByRegionAndSports(Long region, String sports);
    List<Match> findAllByWriter(String writer);
}
