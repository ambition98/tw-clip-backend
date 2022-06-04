package com.example.isedolclipbackend.web.repository;

import com.example.isedolclipbackend.web.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    AccountEntity findById(long id);
    boolean existsById(long id);

    @Query(value = "select a.TwitchAccessToken from AccountEntity a where a.id = ?1")
    String getAccessTokenById(long id);
}
