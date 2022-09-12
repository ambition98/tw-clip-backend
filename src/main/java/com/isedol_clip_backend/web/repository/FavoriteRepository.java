package com.isedol_clip_backend.web.repository;

import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.FavoriteEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends PagingAndSortingRepository<FavoriteEntity, Long> {
    List<FavoriteEntity> findAllByAccount(AccountEntity account);
    List<FavoriteEntity> findByAccount(AccountEntity account, Pageable pageable);
    Optional<FavoriteEntity> findByAccountAndClipId(AccountEntity account, String clipId);
    boolean existsByAccountAndClipId(AccountEntity account, String clipId);
}
