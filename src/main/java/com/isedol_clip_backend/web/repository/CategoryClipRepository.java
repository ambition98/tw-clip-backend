package com.isedol_clip_backend.web.repository;

import com.isedol_clip_backend.web.entity.CategoryClipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryClipRepository extends JpaRepository<CategoryClipEntity, Long> {

//    List<CategoryClipEntity> findByCategorys(long id);
}
