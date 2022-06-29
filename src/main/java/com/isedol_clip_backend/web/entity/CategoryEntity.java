package com.isedol_clip_backend.web.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "CATEGORY")
public class CategoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @ManyToOne
//    @JoinColumn(name = "ACCOUNT_ID")
//    private AccountEntity accountId;
    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    @ToString.Exclude
    private AccountEntity account;

    @Column(name = "CATEGORY_NAME", length = 100)
    private String categoryName;

//    @OneToMany(mappedBy = "category")
//    @ToString.Exclude
//    private List<CategoryClipEntity> categoryClips;
}
