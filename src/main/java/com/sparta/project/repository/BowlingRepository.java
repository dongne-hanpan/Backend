package com.sparta.project.repository;

import com.sparta.project.model.Bowling;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BowlingRepository extends JpaRepository<Bowling, Long> {

    List<Bowling> findAllByUser(User user);
    boolean existsByUserAndMatch(User user, Match match);

}


