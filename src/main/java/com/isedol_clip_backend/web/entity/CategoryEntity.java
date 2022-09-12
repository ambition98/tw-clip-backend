package com.isedol_clip_backend.web.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Table(name = "CATEGORY")
@Entity
public class CategoryEntity extends GeneralEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    @ToString.Exclude
    private AccountEntity account;

    @Column(name = "CATEGORY_NAME", length = 20)
    private String categoryName;

//    @Column(name = "REGDATE", length = 20)
//    @CreationTimestamp
//    private Date regdate;
}
