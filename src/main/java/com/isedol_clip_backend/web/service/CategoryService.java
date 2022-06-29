package com.isedol_clip_backend.web.service;

import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.CategoryEntity;
import com.isedol_clip_backend.web.repository.AccountRepository;
import com.isedol_clip_backend.web.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public CategoryEntity getCategoryById(long id) throws NoExistedDataException {
        return categoryRepository.findById(id)
                .orElseThrow(NoExistedDataException::new);
    }

    public List<CategoryEntity> getCategorysByAccount(AccountEntity entity) throws NoExistedDataException {
        List<CategoryEntity> list = categoryRepository.findByAccount(entity);
        if(list.size() < 1)
            throw new NoExistedDataException();

        return list;

    }

    public CategoryEntity save(long accountId, String categoryName) throws NoExistedDataException {
        CategoryEntity categoryEntity = new CategoryEntity();
        AccountEntity accountEntity = accountRepository.findById(accountId)
                .orElseThrow(NoExistedDataException::new);

        categoryEntity.setAccount(accountEntity);
        categoryEntity.setCategoryName(categoryName);

        return categoryRepository.save(categoryEntity);
    }
}
