package com.isedol_clip_backend.web.controller.user;

import com.isedol_clip_backend.exception.InvalidParameterException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.model.Category;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categorys")
    public ResponseEntity<CommonResponse> getCategorys() throws NoExistedDataException {
        long accountId = getAccountId();
        List<Category> categoryList = categoryService.getByAccountId(accountId);

        return MakeResp.make(HttpStatus.OK, "Success", categoryList);
    }

    @PostMapping("/category")
    public ResponseEntity<CommonResponse> postCategory(@RequestBody final String body)
            throws NoExistedDataException, InvalidParameterException {
        long accountId = getAccountId();
        JSONObject jsonObject = new JSONObject(body);
        String categoryName = jsonObject.getString("categoryName");

        checkValidCategoryName(accountId, categoryName);
        Category categoryDto = categoryService.save(accountId, categoryName);

        return MakeResp.make(HttpStatus.OK, "Success", categoryDto);
    }

    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<CommonResponse> deleteCategory(@PathVariable final long categoryId) throws NoExistedDataException {
        long accountId = getAccountId();
        categoryService.delete(accountId, categoryId);

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    private void checkValidCategoryName(long accountId, String categoryName) throws InvalidParameterException,
            NoExistedDataException {
        if(categoryName.length() < 1 || categoryName.length() > 20) {
            throw new InvalidParameterException("1~20 글자 사이로 입력해주세요");
        }

        if(categoryService.exists(accountId, categoryName)) {
            throw new InvalidParameterException("이미 존재하는 카테고리 이름입니다.");
        }
    }

    private long getAccountId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }
}
