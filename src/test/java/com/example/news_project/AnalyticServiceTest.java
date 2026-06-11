package com.example.news_project;

import com.example.news_project.entity.NewsArticle;
import com.example.news_project.entity.NewsCategory;
import com.example.news_project.entity.NewsSource;
import com.example.news_project.modelDto.ArticleSearchCriteria;
import com.example.news_project.modelDto.CategoryStatistics;
import com.example.news_project.repository.NewsArticleRepository;
import com.example.news_project.service.AnalyticService;
import com.example.news_project.service.ArticleSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticServiceTest {

    @Mock
    private ArticleSearchService searchService;

    @Mock
    private NewsArticleRepository articleRepository;

    @InjectMocks
    private AnalyticService analyticService;

    @Test
    void countByCategoryReturnsCategoriesSortedByCountDescending() {
        when(articleRepository.findAll()).thenReturn(List.of(
                article(NewsCategory.SPORT, null),
                article(NewsCategory.SPORT, null),
                article(NewsCategory.TECHNOLOGY, null),
                article(NewsCategory.OTHER, null),
                article(NewsCategory.SPORT, null)
        ));

        List<CategoryStatistics> statistics = analyticService.countByCategory();

        assertThat(statistics)
                .extracting(CategoryStatistics::getNewsCategory, CategoryStatistics::getCount)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(NewsCategory.SPORT, 3L),
                        org.assertj.core.groups.Tuple.tuple(NewsCategory.TECHNOLOGY, 1L),
                        org.assertj.core.groups.Tuple.tuple(NewsCategory.OTHER, 1L)
                );
    }

    @Test
    void trendingKeywordsAggregatesKeywordsAndPassesDateRangeToSearch() {
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 10, 23, 59);
        when(searchService.search(org.mockito.ArgumentMatchers.any(ArticleSearchCriteria.class))).thenReturn(List.of(
                article(NewsCategory.TECHNOLOGY, "ai, java, ai"),
                article(NewsCategory.SCIENCE, "space, ai"),
                article(NewsCategory.SPORT, null),
                article(NewsCategory.ECONOMY, "java")
        ));

        Map<String, Long> result = analyticService.trendingKeywords(to, 2, from);

        assertThat(result).containsExactly(
                Map.entry("ai", 3L),
                Map.entry("java", 2L)
        );

        ArgumentCaptor<ArticleSearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);
        verify(searchService).search(criteriaCaptor.capture());
        ArticleSearchCriteria criteria = criteriaCaptor.getValue();
        assertThat(criteria.getFrom()).isEqualTo(from);
        assertThat(criteria.getTo()).isEqualTo(to);
        assertThat(criteria.getSortBy()).isEqualTo("date");
    }

    private static NewsArticle article(NewsCategory category, String keywords) {
        NewsArticle article = new NewsArticle("Title", "https://example.com/" + Math.random(), new NewsSource("Source", "https://source.test"));
        article.setCategory(category);
        article.setKeywords(keywords);
        return article;
    }
}
