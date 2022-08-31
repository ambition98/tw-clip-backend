package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.ExpiredRefreshToken;
import com.isedol_clip_backend.exception.InvalidJwtException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.CookieUtil;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AccountService accountService;

    @GetMapping("/test")
    public ResponseEntity<CommonResponse> test() {
        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @GetMapping("/refresh")
    public ResponseEntity<CommonResponse> refreshAccessToken(HttpServletRequest request,
                                                             HttpServletResponse response) throws NoExistedDataException, ExpiredRefreshToken, InvalidJwtException {
        String jwt = CookieUtil.getCookie(request, "tk");
        if(jwt == null || jwt.isEmpty()) {
            throw new InvalidJwtException();
        }
        Long id = JwtTokenProvider.getIdWithoutValidate(jwt);
        if(id == null) {
            throw new InvalidJwtException();
        }

        AccountEntity entity = accountService.getById(id);
        String refreshToken = entity.getRefreshToken();

        try {
            JwtTokenProvider.getTokenClaims(refreshToken);
        } catch (Exception e) {
            throw new ExpiredRefreshToken("다시 로그인 해 주세요");
        }

        String accessToken = JwtTokenProvider.generateUserToken(id);
        CookieUtil.setCookie(response, accessToken);

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(HttpServletResponse response) throws NoExistedDataException {
        long id = getAccountId();
        if(id != -1) {
            AccountEntity accountEntity = accountService.getById(id);
            accountEntity.setRefreshToken(null);
            accountEntity.setTwitchAccessToken(null);
            accountEntity.setTwitchRefreshToken(null);
            accountService.save(accountEntity);
        }

        CookieUtil.deleteCookie(response);

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    private long getAccountId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        if(id.equals("anonymousUser")) {
            return -1L;
        }
        return Long.parseLong(id);
    }
}
