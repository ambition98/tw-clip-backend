package com.example.isedolclipbackend.web.repository;

import com.example.isedolclipbackend.web.entity.CategoryClipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryClipRepository extends JpaRepository<CategoryClipEntity, Long> {

    List<CategoryClipEntity> findByCategoryId(long id);
}
