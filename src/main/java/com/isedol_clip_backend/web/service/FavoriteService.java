package com.isedol_clip_backend.web.service;

import com.isedol_clip_backend.exception.AlreadyExistedDataException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.FavoriteEntity;
import com.isedol_clip_backend.web.model.Favorite;
import com.isedol_clip_backend.web.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    public List<FavoriteEntity> getAllByAccountId(long id) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(id);
        List<FavoriteEntity> entityList
                = favoriteRepository.findAllByAccount(accountEntity);

        if(entityList.size() < 1)
            throw new NoExistedDataException();

        return entityList;
    }

//    public List<String> getByAccountId(long id, Pageable pageable) throws NoExistedDataException {
//        AccountEntity accountEntity = accountService.getById(id);
//        List<FavoriteEntity> entityList
//                = favoriteRepository.findByAccount(accountEntity, pageable);
//        if(entityList.size() < 1)
//            throw new NoExistedDataException();
//
//        for(FavoriteEntity entity : entityList) {
//            System.out.println(entity.toString());
//        }
//
//        List<String> list = new ArrayList<>();
//        entityList.forEach((e) -> list.add(e.getClipId()));
//
//        return list;
//    }

    public boolean exists(long accountId, String clipId) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);
        return favoriteRepository.existsByAccountAndClipId(accountEntity, clipId);
    }

    public Favorite save(long accountId, String clipId) throws NoExistedDataException, AlreadyExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);

        FavoriteEntity favoriteEntity = new FavoriteEntity();
        favoriteEntity.setAccount(accountEntity);
        favoriteEntity.setClipId(clipId);

        boolean exists = favoriteRepository.existsByAccountAndClipId(accountEntity, clipId);
        if(exists)
            throw new AlreadyExistedDataException("이미 추가된 클립 입니다.");

        favoriteEntity = favoriteRepository.save(favoriteEntity);

        return modelMapper.map(favoriteEntity, Favorite.class);
    }

    public void delete(long accountId, String clipId) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);
        FavoriteEntity favoriteEntity
                = favoriteRepository.findByAccountAndClipId(accountEntity, clipId)
                .orElseThrow(NoExistedDataException::new);
        favoriteRepository.delete(favoriteEntity);
    }
}
