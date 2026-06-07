package com.example.news_project.repository;

import com.example.news_project.entity.NewsArticle;
import com.example.news_project.entity.NewsCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    boolean existsByCanonicalUrl(String canonicalUrl);

    boolean existsByTitleIgnoreCaseAndSource_NameIgnoreCase(String title, String sourceName);

    List<NewsArticle> findTop20ByOrderByPublishedAtDesc();

    @Query("""
            select article
            from NewsArticle article
            join fetch article.source source
            where (:keyword is null or lower(concat(article.title, ' ', coalesce(article.summary, ''), ' ', coalesce(article.content, ''), ' ', coalesce(article.keywords, ''))) like lower(concat('%', :keyword, '%')))
              and (:sourceName is null or lower(source.name) like lower(concat('%', :sourceName, '%')))
              and (:category is null or article.category = :category)
              and (:fromDate is null or article.publishedAt >= :fromDate)
              and (:toDate is null or article.publishedAt <= :toDate)
            """)
    List<NewsArticle> search(
            @Param("keyword") String keyword,
            @Param("sourceName") String sourceName,
            @Param("category") NewsCategory category,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Sort sort
    );
}
