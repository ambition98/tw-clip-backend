package com.isedol_clip_backend.web.service;

import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.web.entity.CategoryClipEntity;
import com.isedol_clip_backend.web.entity.CategoryEntity;
import com.isedol_clip_backend.web.repository.CategoryClipRepository;
import com.isedol_clip_backend.web.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryClipService {

    private final CategoryClipRepository categoryClipRepository;
    private final CategoryRepository categoryRepository;

    public List<CategoryClipEntity> getByCategory(CategoryEntity entity) throws NoExistedDataException {
        List<CategoryClipEntity> list = categoryClipRepository.findByCategory(entity);
        if(list.size() < 1)
            throw new NoExistedDataException();

        return list;
    }
}
