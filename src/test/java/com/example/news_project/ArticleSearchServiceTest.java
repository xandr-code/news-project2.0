package com.example.news_project;

import com.example.news_project.entity.NewsCategory;
import com.example.news_project.modelDto.ArticleSearchCriteria;
import com.example.news_project.repository.NewsArticleRepository;
import com.example.news_project.service.ArticleSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleSearchServiceTest {

    @Mock
    private NewsArticleRepository articleRepository;

    @InjectMocks
    private ArticleSearchService service;

    @Test
    void latestDelegatesToRepository() {
        when(articleRepository.findTop20ByOrderByPublishedAtDesc()).thenReturn(List.of());

        assertThat(service.latest()).isEmpty();

        verify(articleRepository).findTop20ByOrderByPublishedAtDesc();
    }

    @Test
    void sortByUsesDateDescendingByDefault() {
        assertSort(service.sortBy(null), Sort.Direction.DESC, "publishedAt");
        assertSort(service.sortBy(""), Sort.Direction.DESC, "publishedAt");
        assertSort(service.sortBy("unknown"), Sort.Direction.DESC, "publishedAt");
        assertSort(service.sortBy("date"), Sort.Direction.DESC, "publishedAt");
    }

    @Test
    void sortBySupportsSourceAndPopularity() {
        assertSort(service.sortBy("source"), Sort.Direction.ASC, "source.name");
        assertSort(service.sortBy("popularity"), Sort.Direction.DESC, "popularity");
    }

    @Test
    void searchPassesCriteriaAndSortToRepository() {
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 10, 0, 0);
        ArticleSearchCriteria criteria = new ArticleSearchCriteria("java", "habr", NewsCategory.TECHNOLOGY, from, to, "source");

        service.search(criteria);

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(articleRepository).search(eq("java"), eq("habr"), eq(NewsCategory.TECHNOLOGY), eq(from), eq(to), sortCaptor.capture());
        assertSort(sortCaptor.getValue(), Sort.Direction.ASC, "source.name");
    }

    private static void assertSort(Sort sort, Sort.Direction direction, String property) {
        Sort.Order order = sort.getOrderFor(property);
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(direction);
    }
}
