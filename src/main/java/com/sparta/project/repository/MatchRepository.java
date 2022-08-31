package com.sparta.project.repository;

import com.sparta.project.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByRegion(Long region);
}
