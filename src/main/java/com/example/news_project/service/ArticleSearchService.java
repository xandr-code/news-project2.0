package com.example.news_project.service;

import com.example.news_project.entity.NewsArticle;
import com.example.news_project.modelDto.ArticleSearchCriteria;
import com.example.news_project.repository.NewsArticleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ArticleSearchService{
    private final NewsArticleRepository articleRepository;


    public ArticleSearchService(NewsArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<NewsArticle> latest(){
        return articleRepository.findTop20ByOrderByPublishedAtDesc();
    }

    public List<NewsArticle> search(ArticleSearchCriteria searchCriteria) {
        return articleRepository.search(searchCriteria.getKeyword(), searchCriteria.getSourceName(), searchCriteria.getCategory(), searchCriteria.getFrom(), searchCriteria.getTo(), sortBy(searchCriteria.getSortBy()));
    }

    public Sort sortBy(String sortedby){
        if (sortedby == null || sortedby.isBlank() || sortedby.equalsIgnoreCase("date")) {
            return Sort.by(Sort.Direction.DESC, "publishedAt");
        } else if (sortedby.equalsIgnoreCase("source")) {
            return Sort.by(Sort.Direction.ASC, "source.name");
        } else if (sortedby.equalsIgnoreCase("popularity")){
            return Sort.by(Sort.Direction.DESC, "popularity");
        } else return Sort.by(Sort.Direction.DESC, "publishedAt");
    }

}
