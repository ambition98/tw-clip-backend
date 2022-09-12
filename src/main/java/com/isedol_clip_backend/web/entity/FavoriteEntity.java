package com.isedol_clip_backend.web.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Table(name = "FAVORITE")
@Entity
public class FavoriteEntity extends ClipsEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    @ToString.Exclude
    private AccountEntity account;

//    @Column(name = "CLIP_ID", length = 100)
//    private String clipId;

//    @Column(name = "REGDATE", length = 20)
//    @CreationTimestamp
//    private Date regdate;
}
