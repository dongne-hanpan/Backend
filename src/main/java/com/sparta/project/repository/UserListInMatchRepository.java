package com.sparta.project.repository;

import com.sparta.project.entity.Match;
import com.sparta.project.entity.User;
import com.sparta.project.entity.UserListInMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserListInMatchRepository extends JpaRepository<UserListInMatch, Long> {
    List<UserListInMatch> findAllByMatchId(Long match_id);
    List<UserListInMatch> findAllByUser(User user);
    UserListInMatch findByMatchAndUser(Match match, User user);
    boolean existsByMatchAndUser(Match match, User user);
    Long countByMatch(Match match);
}
