package com.isedol_clip_backend.web.repository;

import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.CategoryClipEntity;
import com.isedol_clip_backend.web.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryClipRepository extends JpaRepository<CategoryClipEntity, Long> {
    public List<CategoryClipEntity> findByAccountAndCategory(AccountEntity account, CategoryEntity category);
}
