package com.isedol_clip_backend.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isedol_clip_backend.exception.AlreadyExistedDataException;
import com.isedol_clip_backend.exception.ApiRequestException;
import com.isedol_clip_backend.exception.InvalidParameterException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.util.TwitchMapper;
import com.isedol_clip_backend.web.model.Category;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.service.CategoryClipService;
import com.isedol_clip_backend.web.service.CategoryService;
import com.isedol_clip_backend.web.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AccountController {

    private final CategoryService categoryService;
    private final CategoryClipService categoryClipService;
    private final CallTwitchAPI callTwitchApi;
    private final ObjectMapper objectMapperSe;
    private final FavoriteService favoriteService;
    private final TwitchMapper twitchMapper;

    @GetMapping("")
    public ResponseEntity<CommonResponse> getUserByToken() throws IOException, ApiRequestException, NoExistedDataException {

        long id = getAccountId();
        log.info("id: {}", id);
        JSONArray jsonArray = callTwitchApi.requestUser(new long[]{id}, null);
        String jsonString = jsonArray.getJSONObject(0).toString();
        TwitchUser user = objectMapperSe.readValue(jsonString, TwitchUser.class);

        return MakeResp.make(HttpStatus.OK, "Success", user);
    }

    @GetMapping("/favorites")
    public ResponseEntity<CommonResponse> getFavorites(final String cursor) throws NoExistedDataException, ApiRequestException, IOException, ParseException {
        List<String> idList =  favoriteService.getFavoritesByAccountId(getAccountId());
        String[] idArr = idList.toArray(idList.toArray(new String[0]));

        JSONObject jsonObject = callTwitchApi.requestClipsById(idArr);
        TwitchClip[] clips = twitchMapper.mappingClips(jsonObject);

        return MakeResp.make(HttpStatus.OK, "Success", clips);
    }

    @PostMapping("/favorite")
    public ResponseEntity<CommonResponse> postFavorite(@RequestBody final String body) throws NoExistedDataException,
            AlreadyExistedDataException {
        JSONObject jsonObject = new JSONObject(body);
        String clipId = jsonObject.getString("clipId");
        log.info("clipId: {}", clipId);
        favoriteService.save(getAccountId(), clipId);
        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @DeleteMapping("/favorite/{clipId}")
    public ResponseEntity<CommonResponse> deleteFavorite(@PathVariable final String clipId) throws NoExistedDataException {
        favoriteService.delete(getAccountId(), clipId);
        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @GetMapping("/favorite/exists")
    public ResponseEntity<CommonResponse> isExistedFavorite(final String clipId)
            throws NoExistedDataException {
        log.info("clipId: {}", clipId);
        boolean exists = favoriteService.exists(getAccountId(), clipId);

        return MakeResp.make(HttpStatus.OK, "Success", exists);
    }

    @GetMapping("/categorys")
    public ResponseEntity<CommonResponse> getCategorys() throws NoExistedDataException {
        long accountId = getAccountId();
        List<Category> categoryList = categoryService.getCategorysByAccountId(accountId);

        return MakeResp.make(HttpStatus.OK, "Success", categoryList);
    }

    @PostMapping("/category")
    public ResponseEntity<CommonResponse> postCategory(@RequestBody final String body)
            throws NoExistedDataException, InvalidParameterException {
        long accountId = getAccountId();
        JSONObject jsonObject = new JSONObject(body);
        String categoryName = jsonObject.getString("categoryName");

        checkValidCategoryName(categoryName);
        categoryService.save(accountId, categoryName);

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    private void checkValidCategoryName(String s) throws InvalidParameterException {
        if(s.length() < 1 || s.length() > 20) {
            throw new InvalidParameterException("카테고리 이름은 1~20 사이로 입력해야합니다.");
        }
    }

//    @GetMapping("/category/{categoryId}/clips") //추후수정 accountId 추가
//    public ResponseEntity<CommonResponse> getClipsByCategoryId(@PathVariable final int categoryId) {
//        log.info("getClips");
//        long id = getAccountId();
//        List<CategoryClipEntity> clipEntitys = null;
//        try {
//            clipEntitys = categoryClipService.getByCategory(categoryService.getCategoryById(categoryId));
//        } catch (NoExistedDataException e) {
//            log.warn("No Content");
//            return MakeResp.make(HttpStatus.BAD_REQUEST, "No Content");
//        }
//
//        log.info("size: {}", clipEntitys.size());
//        List<RespCategoryClipDto> dtoList = new ArrayList<>(clipEntitys.size());
//        for(CategoryClipEntity entity : clipEntitys) {
//            log.info("entity: {}", entity);
//            RespCategoryClipDto dto = modelMapper.map(entity, RespCategoryClipDto.class);
//            dtoList.add(dto);
//        }
//
//        return MakeResp.make(HttpStatus.OK, "Success", dtoList);
//    }

    private long getAccountId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }
}
