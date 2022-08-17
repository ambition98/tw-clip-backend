package com.isedol_clip_backend.web.repository;

import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByAccount(AccountEntity entity);
    boolean existsByAccountAndCategoryName(AccountEntity entity, String name);
}
