package com.example.news_project.modelDto;

import com.example.news_project.entity.NewsCategory;

import java.time.LocalDateTime;

public class ArticleSearchCriteria {
    String keyword;
    String sourceName;
    NewsCategory category;
    LocalDateTime from;
    LocalDateTime to;
    String sortBy;

    public ArticleSearchCriteria() {

    }

    public ArticleSearchCriteria(String keyword, String sourceName, NewsCategory category, LocalDateTime from, LocalDateTime to, String sortBy) {
        this.keyword = keyword;
        this.sourceName = sourceName;
        this.category = category;
        this.from = from;
        this.to = to;
        this.sortBy = sortBy;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getSourceName() {
        return sourceName;
    }

    public NewsCategory getCategory() {
        return category;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setCategory(NewsCategory category) {
        this.category = category;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
