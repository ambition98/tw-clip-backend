package com.isedol_clip_backend.web.service;

import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.CategoryClipEntity;
import com.isedol_clip_backend.web.entity.CategoryEntity;
import com.isedol_clip_backend.web.repository.CategoryClipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryClipService {

    private final CategoryClipRepository categoryClipRepository;
    private final CategoryService categoryService;
    private final AccountService accountService;


    public List<String> getClips(long accountId, long categoryId) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);
        CategoryEntity categoryEntity = categoryService.getById(categoryId);

        List<CategoryClipEntity> entityList
                = categoryClipRepository.findByAccountAndCategory(accountEntity, categoryEntity);
        if(entityList.size() < 1) {
            throw new NoExistedDataException();
        }

        List<String> clipIdList = new ArrayList<>(entityList.size());
        entityList.forEach(e -> {
            clipIdList.add(e.getClipId());
        });

        return clipIdList;
    }

    public void save(long accountId, long categoryId, String clipId) throws NoExistedDataException {
        CategoryClipEntity categoryClipEntity = new CategoryClipEntity();
        AccountEntity accountEntity = accountService.getById(accountId);
        CategoryEntity categoryEntity = categoryService.getById(categoryId);

        categoryClipEntity.setAccount(accountEntity);
        categoryClipEntity.setCategory(categoryEntity);
        categoryClipEntity.setClipId(clipId);

        categoryClipRepository.save(categoryClipEntity);
    }
}
