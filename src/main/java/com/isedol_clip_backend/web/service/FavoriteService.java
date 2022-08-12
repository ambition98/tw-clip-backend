package com.isedol_clip_backend.web.service;

import com.isedol_clip_backend.exception.AlreadyExistedDataException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.FavoriteEntity;
import com.isedol_clip_backend.web.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AccountService accountService;

    public List<String> getFavoritesByAccountId(long id) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(id);
        List<FavoriteEntity> entityList = favoriteRepository.findByAccount(accountEntity);
        if(entityList.size() < 1)
            throw new NoExistedDataException();

        List<String> list = new ArrayList<>();
        entityList.forEach((e) -> list.add(e.getClipId()));

        return list;
    }

    public boolean exists(long accountId, String clipId) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);
        return favoriteRepository.existsByAccountAndClipId(accountEntity, clipId);
    }

    public void save(long accountId, String clipId) throws NoExistedDataException, AlreadyExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);
        FavoriteEntity favoriteEntity = new FavoriteEntity(accountEntity, clipId);
        boolean exists = favoriteRepository.existsByAccountAndClipId(accountEntity, clipId);
        if(exists)
            throw new AlreadyExistedDataException("이미 추가된 클립 입니다.");
        favoriteRepository.save(favoriteEntity);
    }

    public void delete(long accountId, String clipId) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);
        FavoriteEntity favoriteEntity = favoriteRepository.findByAccountAndClipId(accountEntity, clipId);
        favoriteRepository.delete(favoriteEntity);
    }
}
