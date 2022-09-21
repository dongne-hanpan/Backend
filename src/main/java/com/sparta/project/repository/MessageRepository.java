package com.sparta.project.repository;

import com.sparta.project.entity.Match;
import com.sparta.project.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository <Message, Long> {

    List<Message> findAllByMatchOrderByCreatedAt(Match match);

    void deleteByMatch(Match match);


}
