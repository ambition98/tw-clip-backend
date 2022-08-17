package com.isedol_clip_backend.web.service;

import com.isedol_clip_backend.web.repository.CategoryClipRepository;
import com.isedol_clip_backend.web.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryClipService {

    private final CategoryClipRepository categoryClipRepository;
    private final CategoryRepository categoryRepository;

}
