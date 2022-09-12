package com.isedol_clip_backend.web.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class ClipsEntity extends GeneralEntity {
    @Column(name = "CLIP_ID", length = 100)
    private String clipId;
}
