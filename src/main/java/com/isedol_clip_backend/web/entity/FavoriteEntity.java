package com.isedol_clip_backend.web.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Table(name = "FAVORITE")
@Entity
@NoArgsConstructor
public class FavoriteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    @ToString.Exclude
    private AccountEntity account;

    @Column(name = "CLIP_ID", length = 100)
    private String clipId;

    public FavoriteEntity(AccountEntity account, String clipId) {
        this.account = account;
        this.clipId = clipId;
    }
}
