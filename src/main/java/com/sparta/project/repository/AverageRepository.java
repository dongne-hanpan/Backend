package com.sparta.project.repository;

import com.sparta.project.model.Average;
import com.sparta.project.model.Match;
import com.sparta.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AverageRepository extends JpaRepository<Average, Long> {

    List<Average> findAllByUser(User user);
    boolean existsByUserAndMatch(User user, Match match);

}


