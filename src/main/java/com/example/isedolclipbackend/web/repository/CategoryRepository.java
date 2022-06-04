package com.example.isedolclipbackend.web.repository;

import com.example.isedolclipbackend.web.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    CategoryEntity findById(long id);
    List<CategoryEntity> findByAccountId(long id);
}
