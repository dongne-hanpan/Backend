package com.sparta.project.model;

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
    private String username;

    @Column
    private String comment;

    @Column
    private String mannerPoint;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
