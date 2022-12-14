package com.sparta.project.entity;

import com.sparta.project.dto.user.InviteResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class RequestUserList {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String nickname;

    @Column
    private Long averageScore;

    @Column
    private double mannerPoint;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    public RequestUserList(InviteResponseDto inviteResponseDto, Match match) {
        this.nickname = inviteResponseDto.getNickname();
        this.averageScore = inviteResponseDto.getAverageScore();
        this.mannerPoint = inviteResponseDto.getMannerPoint();
        this.match = match;
    }

}
