package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.CategoryClipEntity;
import com.isedol_clip_backend.web.entity.CategoryEntity;
import com.isedol_clip_backend.web.model.request.ReqCategoryDto;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.model.response.RespCategoryClipDto;
import com.isedol_clip_backend.web.model.response.RespCategoryDto;
import com.isedol_clip_backend.web.service.AccountService;
import com.isedol_clip_backend.web.service.CategoryClipService;
import com.isedol_clip_backend.web.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final CategoryService categoryService;
    private final CategoryClipService categoryClipService;

    private final ModelMapper modelMapper;

    @GetMapping("/test")
    public ResponseEntity<CommonResponse> getTwitchUser() {

        Authentication authentication
                = SecurityContextHolder.getContext().getAuthentication();

        log.info("auth: {}", authentication);
        log.info("principal: {}", authentication.getPrincipal());
        log.info("authorities: {}", authentication.getAuthorities());
        log.info("credencial: {}", authentication.getCredentials());
        log.info("details: {}", authentication.getDetails());
        log.info("name: {}", authentication.getName());

        return null;
    }

    @PostMapping("/category")
    public ResponseEntity<CommonResponse> postCategory(@Valid final ReqCategoryDto dto) {
        long id = getAccountId();

        try {
            AccountEntity accountEntity = accountService.getById(id);
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setCategoryName(dto.getCategoryName());
            categoryEntity.setAccount(accountEntity);

            CategoryEntity resCategory = categoryService.save(id, dto.getCategoryName());

        } catch (NoExistedDataException e) {
            return MakeResp.make(HttpStatus.BAD_REQUEST, "No Content");
        }

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @GetMapping("/categorys")
    public ResponseEntity<CommonResponse> getCategorys() {
        long id = getAccountId();

        List<CategoryEntity> categoryEntitys = null;
        try {
            categoryEntitys = categoryService.getCategorysByAccount(accountService.getById(id));
        } catch (NoExistedDataException e) {
            return MakeResp.make(HttpStatus.BAD_REQUEST, "No Content");
        }

        List<RespCategoryDto> dtoList = new ArrayList<>(categoryEntitys.size());
        for(CategoryEntity entity : categoryEntitys) {
            RespCategoryDto dto = modelMapper.map(entity, RespCategoryDto.class);
            dtoList.add(dto);
        }

        return MakeResp.make(HttpStatus.OK, "Success", dtoList);
    }

    @GetMapping("/category/{categoryId}/clips")
    public ResponseEntity<CommonResponse> getClipsByCategoryId(@PathVariable final int categoryId) {
        log.info("getClips");
        long id = getAccountId();
        List<CategoryClipEntity> clipEntitys = null;
        try {
            clipEntitys = categoryClipService.getByCategory(categoryService.getCategoryById(categoryId));
        } catch (NoExistedDataException e) {
            log.warn("No Content");
            return MakeResp.make(HttpStatus.BAD_REQUEST, "No Content");
        }

        log.info("size: {}", clipEntitys.size());
        List<RespCategoryClipDto> dtoList = new ArrayList<>(clipEntitys.size());
        for(CategoryClipEntity entity : clipEntitys) {
            log.info("entity: {}", entity);
            RespCategoryClipDto dto = modelMapper.map(entity, RespCategoryClipDto.class);
            dtoList.add(dto);
        }

        return MakeResp.make(HttpStatus.OK, "Success", dtoList);
    }

    private long getAccountId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }
}
