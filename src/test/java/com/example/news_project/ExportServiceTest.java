package com.example.news_project;

import com.example.news_project.entity.NewsArticle;
import com.example.news_project.entity.NewsCategory;
import com.example.news_project.entity.NewsSource;
import com.example.news_project.modelDto.ArticleSearchCriteria;
import com.example.news_project.modelDto.ExportFormat;
import com.example.news_project.service.ArticleSearchService;
import com.example.news_project.service.ExportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private ArticleSearchService articleSearchService;

    @Test
    void renderCsvEscapesQuotedFields() throws IOException {
        ArticleSearchCriteria criteria = new ArticleSearchCriteria();
        ExportService service = new ExportService(articleSearchService);
        when(articleSearchService.search(criteria)).thenReturn(List.of(
                article("Title \"quoted\"; with semicolon", "Summary \"text\"", "https://example.com/a")
        ));

        String csv = service.render(ExportFormat.CSV, criteria);

        assertThat(csv).startsWith("id;publishedAt;source;category;title;url;summary\n");
        assertThat(csv).contains("\"Title \"\"quoted\"\"; with semicolon\"");
        assertThat(csv).contains("\"Summary \"\"text\"\"\"");
    }

    @Test
    void renderJsonEscapesSpecialCharacters() throws IOException {
        ArticleSearchCriteria criteria = new ArticleSearchCriteria();
        ExportService service = new ExportService(articleSearchService);
        NewsArticle article = article("Line\nbreak and \"quote\"", "Summary", "https://example.com/json");
        article.setKeywords("java, testing");
        when(articleSearchService.search(criteria)).thenReturn(List.of(article));

        String json = service.render(ExportFormat.JSON, criteria);

        assertThat(json).contains("\"title\": \"Line\\nbreak and \\\"quote\\\"\"");
        assertThat(json).contains("\"keywords\": \"java, testing\"");
    }

    @Test
    void renderHtmlEscapesUserControlledContent() throws IOException {
        ArticleSearchCriteria criteria = new ArticleSearchCriteria();
        ExportService service = new ExportService(articleSearchService);
        when(articleSearchService.search(criteria)).thenReturn(List.of(
                article("<alert>", "Use & check > all", "https://example.com/?a=1&b=2")
        ));

        String html = service.render(ExportFormat.HTML, criteria);

        assertThat(html).contains("&lt;alert&gt;");
        assertThat(html).contains("Use &amp; check &gt; all");
        assertThat(html).contains("https://example.com/?a=1&amp;b=2");
    }

    private static NewsArticle article(String title, String summary, String url) {
        NewsArticle article = new NewsArticle(title, url, new NewsSource("Test Source", "https://source.test"));
        article.setSummary(summary);
        article.setCategory(NewsCategory.TECHNOLOGY);
        article.setPublishedAt(LocalDateTime.of(2026, 6, 10, 12, 0));
        return article;
    }
}
