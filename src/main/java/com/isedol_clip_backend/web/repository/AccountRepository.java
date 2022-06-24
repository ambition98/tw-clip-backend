package com.isedol_clip_backend.web.repository;

import com.isedol_clip_backend.web.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

//    AccountEntity findById(long id);
//    boolean existsById(long id);
//
//    @Query(value = "select a.TwitchAccessToken from AccountEntity a where a.id = ?1")
//    String getAccessTokenById(long id);
}
