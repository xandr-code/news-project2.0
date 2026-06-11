package com.example.news_project;

import com.example.news_project.config.KeywordExtract;
import com.example.news_project.config.NewsCategorizer;
import com.example.news_project.config.SummaryService;
import com.example.news_project.config.UrlNormalizer;
import com.example.news_project.entity.NewsArticle;
import com.example.news_project.entity.NewsCategory;
import com.example.news_project.entity.NewsSource;
import com.example.news_project.modelDto.ImportResult;
import com.example.news_project.modelDto.NewsCandidate;
import com.example.news_project.repository.NewsArticleRepository;
import com.example.news_project.repository.NewsSourceRepository;
import com.example.news_project.service.ArticleParser;
import com.example.news_project.service.NewsImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsImportServiceTest {

    @Mock
    private NewsSourceRepository sourceRepository;

    @Mock
    private NewsArticleRepository articleRepository;

    @Mock
    private ArticleParser parser;

    @Mock
    private SummaryService summaryService;

    @Mock
    private NewsCategorizer categorizer;

    @Mock
    private KeywordExtract extract;

    @Mock
    private UrlNormalizer urlNormalizer;

    @InjectMocks
    private NewsImportService service;

    @Test
    void refreshAllImportsEnabledSourcesInRepositoryOrder() throws IOException {
        NewsSource first = new NewsSource("A source", "https://a.test/feed");
        NewsSource second = new NewsSource("B source", "https://b.test/feed");
        when(sourceRepository.findAllByEnabledTrueOrderByNameAsc()).thenReturn(List.of(first, second));
        when(parser.fetch(first)).thenReturn(List.of());
        when(parser.fetch(second)).thenReturn(List.of());

        List<ImportResult> results = service.refreshAll();

        assertThat(results).extracting(ImportResult::getSourceName).containsExactly("A source", "B source");
        verify(sourceRepository).findAllByEnabledTrueOrderByNameAsc();
    }

    @Test
    void refreshSourceSavesNewArticlesWithDerivedFields() throws IOException {
        NewsSource source = new NewsSource("Tech", "https://tech.test/feed");
        LocalDateTime publishedAt = LocalDateTime.of(2026, 6, 10, 8, 30);
        NewsCandidate candidate = new NewsCandidate(
                "New Java release",
                "https://tech.test/news?utm_source=feed",
                "",
                "https://tech.test/image.png",
                "Long content ".repeat(40),
                publishedAt
        );
        when(parser.fetch(source)).thenReturn(List.of(candidate));
        when(urlNormalizer.normalize(candidate.getUrl())).thenReturn("https://tech.test/news");
        when(articleRepository.existsByCanonicalUrl("https://tech.test/news")).thenReturn(false);
        when(articleRepository.existsByTitleIgnoreCaseAndSource_NameIgnoreCase("New Java release", "Tech")).thenReturn(false);
        when(summaryService.summarize(candidate.getContent())).thenReturn("Generated summary");
        when(categorizer.detect("New Java release", candidate.getContent() + " Generated summary")).thenReturn(NewsCategory.TECHNOLOGY);
        when(extract.extract("New Java release Generated summary " + candidate.getContent())).thenReturn("java, release");

        ImportResult result = service.refreshSource(source);

        assertThat(result.getSourceName()).isEqualTo("Tech");
        assertThat(result.getFeatured()).isEqualTo(1);
        assertThat(result.getAdded()).isEqualTo(1);
        assertThat(result.getDublicates()).isZero();
        assertThat(result.getMessage()).isEqualTo("OK");

        ArgumentCaptor<NewsArticle> articleCaptor = ArgumentCaptor.forClass(NewsArticle.class);
        verify(articleRepository).save(articleCaptor.capture());
        NewsArticle saved = articleCaptor.getValue();
        assertThat(saved.getTitle()).isEqualTo("New Java release");
        assertThat(saved.getCanonicalUrl()).isEqualTo("https://tech.test/news");
        assertThat(saved.getSummary()).isEqualTo("Generated summary");
        assertThat(saved.getMediaUrl()).isEqualTo("https://tech.test/image.png");
        assertThat(saved.getPublishedAt()).isEqualTo(publishedAt);
        assertThat(saved.getCategory()).isEqualTo(NewsCategory.TECHNOLOGY);
        assertThat(saved.getKeywords()).isEqualTo("java, release");
        assertThat(saved.getPopularity()).isGreaterThanOrEqualTo(1);
        assertThat(saved.getSource()).isSameAs(source);
    }

    @Test
    void refreshSourceSkipsBlankUrlsAndDuplicates() throws IOException {
        NewsSource source = new NewsSource("Tech", "https://tech.test/feed");
        NewsCandidate blankUrl = candidate("Blank", "https://tech.test/blank");
        NewsCandidate duplicateUrl = candidate("Duplicate by url", "https://tech.test/duplicate");
        NewsCandidate duplicateTitle = candidate("Duplicate title", "https://tech.test/title");
        when(parser.fetch(source)).thenReturn(List.of(blankUrl, duplicateUrl, duplicateTitle));
        when(urlNormalizer.normalize(blankUrl.getUrl())).thenReturn("");
        when(urlNormalizer.normalize(duplicateUrl.getUrl())).thenReturn("https://tech.test/duplicate");
        when(urlNormalizer.normalize(duplicateTitle.getUrl())).thenReturn("https://tech.test/title");
        when(articleRepository.existsByCanonicalUrl("https://tech.test/duplicate")).thenReturn(true);
        when(articleRepository.existsByCanonicalUrl("https://tech.test/title")).thenReturn(false);
        when(articleRepository.existsByTitleIgnoreCaseAndSource_NameIgnoreCase("Duplicate title", "Tech")).thenReturn(true);

        ImportResult result = service.refreshSource(source);

        assertThat(result.getFeatured()).isEqualTo(3);
        assertThat(result.getAdded()).isZero();
        assertThat(result.getDublicates()).isEqualTo(3);
        verify(articleRepository, never()).save(org.mockito.ArgumentMatchers.any(NewsArticle.class));
    }

    @Test
    void refreshSourceReturnsErrorResultWhenParserFails() throws IOException {
        NewsSource source = new NewsSource("Broken", "https://broken.test/feed");
        when(parser.fetch(source)).thenThrow(new IOException("network unavailable"));

        ImportResult result = service.refreshSource(source);

        assertThat(result.getSourceName()).isEqualTo("Broken");
        assertThat(result.getFeatured()).isZero();
        assertThat(result.getAdded()).isZero();
        assertThat(result.getDublicates()).isZero();
        assertThat(result.getMessage()).isEqualTo("network unavailable");
        verify(articleRepository, never()).save(org.mockito.ArgumentMatchers.any(NewsArticle.class));
    }

    private static NewsCandidate candidate(String title, String url) {
        return new NewsCandidate(title, url, "Summary", null, "Content", LocalDateTime.of(2026, 6, 10, 12, 0));
    }
}
