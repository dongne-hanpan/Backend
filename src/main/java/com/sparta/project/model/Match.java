package com.sparta.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.project.dto.match.MatchRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Table(name = "matches")
@Entity
@NoArgsConstructor
public class Match extends Timestamped{

    @Id
    @Column(name = "match_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String writer;
    @Column
    private String date;
    @Column
    private String time;
    @Column
    private String place;
    @Column
    private String contents;
    @Column
    private Long region;
    @Column
    private String sports;
    @Column
    private Long matchIntakeFull;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "match_id")
    private List<RequestUserList> requestUserList;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "match_id")
    private List<UserListInMatch> userListInMatches;

    @Builder
    public Match(MatchRequestDto matchRequestDto) {
        this.writer = matchRequestDto.getWriter();
        this.date = matchRequestDto.getDate();
        this.time = matchRequestDto.getTime();
        this.place = matchRequestDto.getPlace();
        this.contents = matchRequestDto.getContents();
        this.region = matchRequestDto.getRegion();
        this.sports = matchRequestDto.getSports();
        this.matchIntakeFull = matchRequestDto.getMatchIntakeFull();
    }

    public void updateMatch(MatchRequestDto matchRequestDto) {
        this.date = matchRequestDto.getDate();
        this.time = matchRequestDto.getTime();
        this.place = matchRequestDto.getPlace();
        this.contents = matchRequestDto.getContents();
        this.region = matchRequestDto.getRegion();
        this.sports = matchRequestDto.getSports();
    }
}
