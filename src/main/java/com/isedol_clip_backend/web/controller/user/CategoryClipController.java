package com.isedol_clip_backend.web.controller.user;

import com.isedol_clip_backend.exception.ApiRequestException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.util.TwitchClipUtil;
import com.isedol_clip_backend.web.entity.CategoryClipEntity;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.service.CategoryClipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CategoryClipController {

    private final CategoryClipService categoryClipService;
    private final TwitchClipUtil twitchClipUtil;

    @GetMapping("/category/{categoryId}/clips")
    public ResponseEntity<CommonResponse> getCategoryClip(
            @PathVariable final long categoryId)
            throws NoExistedDataException, ApiRequestException, IOException, ParseException {

        List<CategoryClipEntity> entityList = categoryClipService.getClips(getAccountId(), categoryId);
        List<TwitchClip> clips = twitchClipUtil.getClipList(entityList);

        return MakeResp.make(HttpStatus.OK, "Success", clips);
    }

    @PostMapping("/category/{categoryId}/clip/{clipId}")
    public ResponseEntity<CommonResponse> postCategoryClip(
            @PathVariable final long categoryId,
            @PathVariable final String clipId)
            throws NoExistedDataException {

        categoryClipService.save(getAccountId(), categoryId, clipId);

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    private long getAccountId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        return Long.parseLong(id);
    }
}
