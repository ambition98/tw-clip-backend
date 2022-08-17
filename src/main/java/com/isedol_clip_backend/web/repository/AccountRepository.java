package com.isedol_clip_backend.web.repository;

import com.isedol_clip_backend.web.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

}
