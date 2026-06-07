package com.example.news_project.bootstrap;

import com.example.news_project.entity.NewsArticle;
import com.example.news_project.entity.NewsSource;
import com.example.news_project.repository.NewsArticleRepository;
import com.example.news_project.repository.NewsSourceRepository;
import com.example.news_project.service.SourceService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializr implements ApplicationRunner {
    private final SourceService sourceService;

    public DataInitializr(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        addIfMissing("Lenta.ru", "https://lenta.ru/rss/news");
        addIfMissing("Habr", "https://habr.com/ru/rss/news/?fl=ru");
        addIfMissing("RIA", "https://ria.ru/export/rss2/archive/index.xml");
        addIfMissing("TASS", "https://tass.ru/rss/v2.xml");
        addIfMissing("Kommersant", "https://www.kommersant.ru/RSS/news.xml");
    }

    public void addIfMissing(String name, String Url){
        if (!sourceService.getSourceRepository().existsByNameIgnoreCase(name)) {
            sourceService.addNewSource(name, Url);
        }
    }

}
