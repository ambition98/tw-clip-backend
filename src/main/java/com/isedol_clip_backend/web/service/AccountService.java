package com.isedol_clip_backend.web.service;

import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountEntity save(AccountEntity entity) {
        AccountEntity res = accountRepository.save(entity);
        log.info(res.toString());

        return res;
    }

    public AccountEntity getById(long id) throws NoExistedDataException {
        return accountRepository.findById(id)
                .orElseThrow(NoExistedDataException::new);
    }

//    public String getAccessTokenById(long id) {
//        return accountRepository.getAccessTokenById(id);
//    }

//    public boolean existsById(long id) {
//        return accountRepository.existsById(id);
//    }
}
