package com.sparta.project.repository;

import com.sparta.project.model.InvitedUser;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitedUserRepository extends JpaRepository<InvitedUser, Long> {
    List<InvitedUser> findAllByMatchId(Long match_id);
    List<InvitedUser> findAllByUser(User user);
    InvitedUser findByMatchAndUser(Match match, User user);
    boolean existsByMatchAndUser(Match match, User user);
}
