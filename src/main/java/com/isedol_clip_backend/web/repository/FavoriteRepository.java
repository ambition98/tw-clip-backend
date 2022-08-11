package com.isedol_clip_backend.web.repository;

import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    List<FavoriteEntity> findByAccount(AccountEntity entity);
    FavoriteEntity findByAccountAndClipId(AccountEntity account, String clipId);
    boolean existsByAccountAndClipId(AccountEntity account, String clipId);
}
