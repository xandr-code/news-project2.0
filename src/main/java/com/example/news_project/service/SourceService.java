package com.example.news_project.service;

import com.example.news_project.entity.NewsSource;
import com.example.news_project.repository.NewsSourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceService {
private final NewsSourceRepository sourceRepository;

    public SourceService(NewsSourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;

    }

    public NewsSource addNewSource(String name, String Url) {
        if (sourceRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Источник с таким названием уже существует");
        }
        return sourceRepository.save(new NewsSource(name, Url));
    }

    public List<NewsSource> findAll(){
        return sourceRepository.findAll();
    }

    public NewsSourceRepository getSourceRepository() {
        return sourceRepository;
    }
}
