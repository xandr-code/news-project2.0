package com.example.news_project.service;

import com.example.news_project.entity.NewsArticle;
import com.example.news_project.modelDto.ArticleSearchCriteria;
import com.example.news_project.modelDto.ExportFormat;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private final ArticleSearchService articleSearchService;

    public ExportService(ArticleSearchService articleSearchService) {
        this.articleSearchService = articleSearchService;
    }

    public Path export(ExportFormat format, ArticleSearchCriteria criteria) throws IOException {
        Files.createDirectories(Path.of("exports"));
        String fileName = "news-" + DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) + "." + format.name().toLowerCase();
        Path output = Path.of("exports", fileName);
        Files.writeString(output, render(format, criteria), StandardCharsets.UTF_8);
        return output.toAbsolutePath();
    }

    public String render(ExportFormat format, ArticleSearchCriteria criteria) throws IOException {
        List<NewsArticle> articles = articleSearchService.search(criteria);
        return switch (format) {
            case CSV -> toCsv(articles);
            case JSON -> toJson(articles);
            case HTML -> toHtml(articles);
        };
    }

    private String toCsv(List<NewsArticle> articles) {
        StringBuilder builder = new StringBuilder("id;publishedAt;source;category;title;url;summary\n");
        for (NewsArticle article : articles) {
            builder.append(article.getId()).append(';')
                    .append(article.getPublishedAt()).append(';')
                    .append(escape(article.getSource().getName())).append(';')
                    .append(article.getCategory()).append(';')
                    .append(escape(article.getTitle())).append(';')
                    .append(escape(article.getCanonicalUrl())).append(';')
                    .append(escape(article.getSummary())).append('\n');
        }
        return builder.toString();
    }

    private String toJson(List<NewsArticle> articles) {
        StringBuilder builder = new StringBuilder("[\n");
        for (int i = 0; i < articles.size(); i++) {
            NewsArticle article = articles.get(i);
            builder.append("  {\n")
                    .append("    \"id\": ").append(article.getId()).append(",\n")
                    .append("    \"title\": \"").append(json(article.getTitle())).append("\",\n")
                    .append("    \"url\": \"").append(json(article.getCanonicalUrl())).append("\",\n")
                    .append("    \"source\": \"").append(json(article.getSource().getName())).append("\",\n")
                    .append("    \"category\": \"").append(json(article.getCategory().getDisplayName())).append("\",\n")
                    .append("    \"summary\": \"").append(json(article.getSummary())).append("\",\n")
                    .append("    \"keywords\": \"").append(json(article.getKeywords())).append("\",\n")
                    .append("    \"publishedAt\": \"").append(article.getPublishedAt()).append("\"\n")
                    .append("  }");
            if (i < articles.size() - 1) {
                builder.append(',');
            }
            builder.append('\n');
        }
        return builder.append(']').toString();
    }
    private String toHtml(List<NewsArticle> articles) {
        StringBuilder builder = new StringBuilder("""
                <!doctype html>
                <html lang="ru">
                <head><meta charset="utf-8"><title>News export</title></head>
                <body><h1>Экспорт новостей</h1>
                """);
        for (NewsArticle article : articles) {
            builder.append("<article><h2>")
                    .append(html(article.getTitle()))
                    .append("</h2><p>")
                    .append(html(article.getSummary()))
                    .append("</p><p><b>")
                    .append(html(article.getSource().getName()))
                    .append("</b> | ")
                    .append(article.getPublishedAt())
                    .append(" | ")
                    .append(article.getCategory().getDisplayName())
                    .append("</p><a href=\"")
                    .append(html(article.getCanonicalUrl()))
                    .append("\">Читать полностью</a></article><hr>");
        }
        return builder.append("</body></html>").toString();
    }

    private String escape(String value) {
        return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
    }

    private String html(String value) {
        return value == null ? "" : value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String json(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}