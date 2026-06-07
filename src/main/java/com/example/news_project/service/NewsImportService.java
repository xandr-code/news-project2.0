package com.example.news_project.service;

import com.example.news_project.config.KeywordExtract;
import com.example.news_project.config.NewsCategorizer;
import com.example.news_project.config.SummaryService;
import com.example.news_project.config.UrlNormalizer;
import com.example.news_project.entity.NewsArticle;
import com.example.news_project.entity.NewsSource;
import com.example.news_project.modelDto.ImportResult;
import com.example.news_project.modelDto.NewsCandidate;
import com.example.news_project.repository.NewsArticleRepository;
import com.example.news_project.repository.NewsSourceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsImportService {
    public static final int maxFoundNews = 20;
    private final NewsSourceRepository sourceRepository;
    private final NewsArticleRepository articleRepository;
    private final ArticleParser parser;
    private final SummaryService summaryService;
    private final NewsCategorizer categorizer;
    private final KeywordExtract extract;
    private final UrlNormalizer urlNormalizer;

    public NewsImportService(NewsSourceRepository sourceRepository, NewsArticleRepository articleRepository, ArticleParser parser, SummaryService summaryService, NewsCategorizer categorizer, KeywordExtract extract, UrlNormalizer urlNormalizer) {
        this.sourceRepository = sourceRepository;
        this.articleRepository = articleRepository;
        this.parser = parser;
        this.summaryService = summaryService;
        this.categorizer = categorizer;
        this.extract = extract;
        this.urlNormalizer = urlNormalizer;
    }
    public List<ImportResult> refreshAll() {
        List<ImportResult> results = new ArrayList<>();
        for (NewsSource source : sourceRepository.findAllByEnabledTrueOrderByNameAsc()) {
            results.add(refreshSource(source));
        }
        return results;
    }

    public ImportResult refreshSource(NewsSource source) {
        try {
            List<NewsCandidate> candidates = parser.fetch(source);
            int added = 0;
            int duplicates = 0;
            int featured = Math.min(candidates.size(), maxFoundNews);

            for (NewsCandidate candidate : candidates.stream().limit(maxFoundNews).toList()) {
                String canonicalUrl = urlNormalizer.normalize(candidate.getUrl());
                if (canonicalUrl.isBlank()
                        || articleRepository.existsByCanonicalUrl(canonicalUrl)
                        || articleRepository.existsByTitleIgnoreCaseAndSource_NameIgnoreCase(candidate.getTitle(), source.getName())) {
                    duplicates++;
                    continue;
                }

                String content = candidate.getContent() == null ? "" : candidate.getContent();
                String summary = candidate.getDescription();
                if (summary == null || summary.isBlank()) {
                    summary = summaryService.summarize(content);
                }

                NewsArticle article = new NewsArticle(candidate.getTitle(), canonicalUrl, source);
                article.setContent(content);
                article.setSummary(summary);
                article.setMediaUrl(candidate.getMediaUrl());
                article.setPublishedAt(candidate.getPublishedAt());
                article.setCategory(categorizer.detect(candidate.getTitle(), content + " " + summary));
                article.setKeywords(extract.extract(candidate.getTitle() + " " + summary + " " + content));
                article.setPopularity(Math.max(1, content.length() / 300));
                articleRepository.save(article);
                added++;
            }

            return new ImportResult(source.getName(), featured, added, duplicates, "OK");
        } catch (Exception e) {
            return new ImportResult(source.getName(), 0, 0, 0, e.getMessage());
        }
    }
}
