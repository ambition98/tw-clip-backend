package com.isedol_clip_backend.web.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
//@ToString
@Entity
@Table(name = "CATEGORY_CLIP")
public class CategoryClipEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @ManyToOne
//    @JoinColumn(name = "CATEGORY_ID")
//    private CategoryEntity categoryId;

    @Column(name = "CLIP_ID", length = 100)
    private String clipId;
}
