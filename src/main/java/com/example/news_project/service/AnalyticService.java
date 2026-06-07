package com.example.news_project.service;

import com.example.news_project.entity.NewsArticle;
import com.example.news_project.entity.NewsCategory;
import com.example.news_project.modelDto.ArticleSearchCriteria;
import com.example.news_project.modelDto.CategoryStatistics;
import com.example.news_project.repository.NewsArticleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticService {
    private final ArticleSearchService searchService;
    private final NewsArticleRepository articleRepository;

    public AnalyticService(ArticleSearchService searchService, NewsArticleRepository articleRepository) {
        this.searchService = searchService;
        this.articleRepository = articleRepository;
    }

    public List<CategoryStatistics> countByCategory(){
        Map<NewsCategory, Long> count = new EnumMap<>(NewsCategory.class);
        for (NewsArticle article : articleRepository.findAll()) {
            count.merge(article.getCategory(), 1L, Long::sum);
        }
        return count.entrySet().stream().map(element-> new CategoryStatistics(element.getKey(), element.getValue()))
                .sorted(Comparator.comparing(CategoryStatistics::getCount).reversed()).toList();
    }

    public Map<String, Long> trendingKeywords(LocalDateTime to, int limit, LocalDateTime from) {
        List<NewsArticle> articles = searchService.search(new ArticleSearchCriteria(null, null, null, from, to, "date"));
        return articles.stream()
                .flatMap(element-> Arrays.stream((element.getKeywords() == null ? "" : element.getKeywords())
                        .split(","))).map(String::trim).filter(keyword->!keyword.isBlank()).collect(Collectors.groupingBy(keyword-> keyword, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (key, value)->key, LinkedHashMap::new));
    }

}

