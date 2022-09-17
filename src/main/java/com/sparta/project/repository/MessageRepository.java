package com.sparta.project.repository;

import com.sparta.project.dto.message.MessageResponseDto;
import com.sparta.project.model.Match;
import com.sparta.project.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository <Message, Long> {

    List<Message> findAllByMatchOrderByCreatedAt(Match match);
    Message findFirstByMatchOrderByCreatedAtDesc(Match match);


}
