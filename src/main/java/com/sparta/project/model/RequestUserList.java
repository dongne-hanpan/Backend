package com.sparta.project.model;

import com.sparta.project.dto.InviteResponseDto;
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

    public RequestUserList(InviteResponseDto inviteResponseDto) {
        this.nickname = inviteResponseDto.getNickname();
        this.averageScore = inviteResponseDto.getAverageScore();
        this.mannerPoint = inviteResponseDto.getMannerPoint();
    }

}
