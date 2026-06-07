package com.example.news_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "news_articles",
        indexes = {
                @Index(name = "idx_article_canonical_url", columnList = "canonicalUrl"),
                @Index(name = "idx_article_published_at", columnList = "publishedAt"),
                @Index(name = "idx_article_category", columnList = "category")
        }
)
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(nullable = false, length = 1024)
    private String canonicalUrl;

    @Column(length = 1024)
    private String mediaUrl;

    @Column(length = 1000)
    private String summary;

    @Lob
    private String content;

    @Column(length = 1000)
    private String keywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsCategory category = NewsCategory.OTHER;

    @Column(nullable = false)
    private LocalDateTime publishedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime collectedAt = LocalDateTime.now();

    @Column(nullable = false)
    private int popularity;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private NewsSource source;

    protected NewsArticle() {
    }

    public NewsArticle(String title, String canonicalUrl, NewsSource source) {
        this.title = title;
        this.canonicalUrl = canonicalUrl;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public NewsCategory getCategory() {
        return category;
    }

    public void setCategory(NewsCategory category) {
        this.category = category;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public NewsSource getSource() {
        return source;
    }
}
