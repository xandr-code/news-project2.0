package com.example.news_project.repository;

import com.example.news_project.entity.NewsSource;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface NewsSourceRepository extends JpaRepository<NewsSource, Long> {

    List<NewsSource> findAllByEnabledTrueOrderByNameAsc();

    Optional<NewsSource> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
