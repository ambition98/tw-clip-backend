package com.isedol_clip_backend.web.service;

import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.CategoryEntity;
import com.isedol_clip_backend.web.model.Category;
import com.isedol_clip_backend.web.repository.AccountRepository;
import com.isedol_clip_backend.web.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    public List<Category> getCategorysByAccountId(long id) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(id);
        List<CategoryEntity> entityList = categoryRepository.findByAccount(accountEntity);
        if(entityList.size() < 1)
            throw new NoExistedDataException();

        List<Category> categoryList = new ArrayList<>();
        entityList.forEach((e) -> {
            Category c = modelMapper.map(e, Category.class);
            categoryList.add(c);
        });

        return categoryList;

    }

    public boolean exists(long accountId, String categoryName) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);
        return categoryRepository.existsByAccountAndCategoryName(accountEntity, categoryName);
    }

    public Category save(long accountId, String categoryName) throws NoExistedDataException {
        CategoryEntity categoryEntity = new CategoryEntity();
        AccountEntity accountEntity = accountService.getById(accountId);

        categoryEntity.setAccount(accountEntity);
        categoryEntity.setCategoryName(categoryName);
        categoryEntity = categoryRepository.save(categoryEntity);
        return modelMapper.map(categoryEntity, Category.class);
    }

    public void delete(long accountId, long categoryId) throws NoExistedDataException {
        AccountEntity accountEntity = accountService.getById(accountId);
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(NoExistedDataException::new);

        if(accountEntity.getId() != categoryEntity.getAccount().getId()) {
            throw new NoExistedDataException();
        }

        categoryRepository.delete(categoryEntity);
    }
}
