package com.example.news_project.console;

import com.example.news_project.entity.NewsArticle;
import com.example.news_project.modelDto.ArticleSearchCriteria;
import com.example.news_project.modelDto.ExportFormat;
import com.example.news_project.modelDto.ImportResult;
import com.example.news_project.service.AnalyticService;
import com.example.news_project.service.ArticleSearchService;
import com.example.news_project.service.ExportService;
import com.example.news_project.service.NewsImportService;
import com.example.news_project.service.SourceService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
@ConditionalOnProperty(name = "app.console.enabled", havingValue = "true", matchIfMissing = true)
public class ConsoleInterface implements ApplicationRunner {
    private final Scanner scanner = new Scanner(System.in);
    private final AnalyticService analyticService;
    private final ArticleSearchService articleSearchService;
    private final SourceService sourceService;
    private final ExportService exportService;
    private final ConfigurableApplicationContext context;
    private final NewsImportService importService;

    public ConsoleInterface(AnalyticService analyticService,
                            ArticleSearchService articleSearchService,
                            SourceService sourceService,
                            ExportService exportService,
                            ConfigurableApplicationContext context,
                            NewsImportService importService) {
        this.analyticService = analyticService;
        this.articleSearchService = articleSearchService;
        this.sourceService = sourceService;
        this.exportService = exportService;
        this.context = context;
        this.importService = importService;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean runner = true;
        while (runner) {
            printInfo();
            String command = scanner.nextLine();
            try {
                runner = handle(command);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        context.close();
    }

    public void printInfo() {
        System.out.println();
        System.out.println("Агрегатор новостей");
        System.out.println("1 - Последние новости");
        System.out.println("2 - Поиск по ключевому слову");
        System.out.println("3 - Обновить новости");
        System.out.println("4 - Популярные темы");
        System.out.println("5 - Статистика по категориям");
        System.out.println("6 - Экспорт новостей");
        System.out.println("7 - Список источников");
        System.out.println("0 - Выход");
        System.out.print("Команда: ");
    }

    public boolean handle(String command) throws Exception {
        if (command.equals("1")) {
            printArticles(articleSearchService.latest());
        } else if (command.equals("2")) {
            System.out.print("Ключевое слово: ");
            String keyword = scanner.nextLine();
            printArticles(articleSearchService.search(new ArticleSearchCriteria(keyword, null, null, null, null, "date")));
        } else if (command.equals("3")) {
            List<ImportResult> results = importService.refreshAll();
            for (ImportResult result : results) {
                System.out.printf("%s: найдено %d, добавлено %d, дублей %d, статус: %s%n",
                        result.getSourceName(), result.getFeatured(), result.getAdded(), result.getDublicates(), result.getMessage());
            }
        } else if (command.equals("4")) {
            Map<String, Long> keywords = analyticService.trendingKeywords(LocalDateTime.now(), 10, LocalDateTime.now().minusDays(30));
            if (keywords.isEmpty()) {
                System.out.println("Тем пока нет. Сначала обновите новости.");
            } else {
                keywords.forEach((keyword, count) -> System.out.println(keyword + " - " + count));
            }
        } else if (command.equals("5")) {
            analyticService.countByCategory().forEach(statistics ->
                    System.out.println(statistics.getNewsCategory().getDisplayName() + " - " + statistics.getCount()));
        } else if (command.equals("6")) {
            System.out.print("Формат (CSV, JSON, HTML): ");
            ExportFormat format = ExportFormat.valueOf(scanner.nextLine().trim().toUpperCase());
            Path path = exportService.export(format, new ArticleSearchCriteria(null, null, null, null, null, "date"));
            System.out.println("Файл создан: " + path);
        } else if (command.equals("7")) {
            sourceService.findAll().forEach(source -> System.out.println(source.getName() + " - " + source.getUrl()));
        } else if (command.equals("0")) {
            return false;
        } else {
            System.out.println("Неизвестная команда");
        }
        return true;
    }

    private void printArticles(List<NewsArticle> articles) {
        if (articles.isEmpty()) {
            System.out.println("Новостей пока нет. Запустите обновление командой 3.");
            return;
        }
        for (NewsArticle article : articles) {
            System.out.println();
            System.out.println("#" + article.getId() + " " + article.getTitle());
            System.out.println(article.getPublishedAt() + " | " + article.getSource().getName() + " | " + article.getCategory().getDisplayName());
            if (article.getSummary() != null && !article.getSummary().isBlank()) {
                System.out.println(article.getSummary());
            }
            System.out.println(article.getCanonicalUrl());
        }
    }
}
