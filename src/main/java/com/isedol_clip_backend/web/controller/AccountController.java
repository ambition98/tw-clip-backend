package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.entity.CategoryEntity;
import com.isedol_clip_backend.web.model.request.ReqCategoryDto;
import com.isedol_clip_backend.web.model.response.RespCategoryDto;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.service.AccountService;
import com.isedol_clip_backend.web.service.CategoryClipService;
import com.isedol_clip_backend.web.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @GetMapping("/user")
//    public ResponseEntity<CommonResponseDto> getUserByToken() {
//
//        int id = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        log.info("Id: {}", id);
//
//        accountService.get
//
//
//
//        return null;
//    }

    @PostMapping("/category")
    public ResponseEntity<CommonResponse> makeCategory(@Valid final ReqCategoryDto dto) {
        log.info("categoryName: {}", dto.getCategoryName());
        long id = getAccountId();

        try {
            AccountEntity accountEntity = accountService.getById(id);
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setCategoryName(dto.getCategoryName());
            categoryEntity.setAccount(accountEntity);

            CategoryEntity resCategory = categoryService.save(id, dto.getCategoryName());

        } catch (NoExistedDataException e) {
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Not Existed Account Id");
        }

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @GetMapping("/categorys")
    public ResponseEntity<CommonResponse> getCategorysByToken() {

        long id = getAccountId();

        List<CategoryEntity> categoryEntitys = null;
        try {
            categoryEntitys = categoryService.getCategorysByAccount(accountService.getById(id));
        } catch (NoExistedDataException e) {
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Not Existed Account Id");
        }

        if(categoryEntitys.size() < 1) {
            return MakeResp.make(HttpStatus.OK, "No Content");
        }
        List<RespCategoryDto> dtoList = new ArrayList<>(categoryEntitys.size());

        for(CategoryEntity entity : categoryEntitys) {
            RespCategoryDto dto = new RespCategoryDto();
            dto.setId(entity.getId());
            dto.setCategoryName(entity.getCategoryName());

            dtoList.add(dto);
        }

        return MakeResp.make(HttpStatus.OK, "Success", dtoList);
    }

//    @GetMapping("/clips")
//    public ResponseEntity<CommonResponse> getClipsByCategoryId(final int CategoryId) {
//
//        return null;
//    }

    private long getAccountId() {
        return Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
