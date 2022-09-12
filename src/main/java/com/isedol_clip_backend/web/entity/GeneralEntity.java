package com.isedol_clip_backend.web.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@MappedSuperclass
public abstract class GeneralEntity {
    @Column(name = "CREATED_AT", length = 20, nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "UPDATED_AT", length = 20, nullable = false)
    @UpdateTimestamp
    private Date updatedAt;
}
