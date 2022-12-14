package com.sparta.project.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Evaluation {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String nickname;

    @Column
    private String comment;

    @Column
    private double mannerPoint;

    @Column
    private Long matchId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Evaluation(String nickname, String comment, double mannerPoint, User user, Long match_id) {
        this.nickname = nickname;
        this.comment = comment;
        this.mannerPoint = mannerPoint;
        this.user = user;
        this.matchId = match_id;
    }
}
