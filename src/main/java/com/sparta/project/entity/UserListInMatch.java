package com.sparta.project.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
//@Setter
@NoArgsConstructor
public class UserListInMatch {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @Builder
    public UserListInMatch(User user, Match match) {
        this.user = user;
        this.match = match;
    }
}
