package com.example.news_project;

import com.example.news_project.repository.NewsSourceRepository;
import com.example.news_project.service.SourceService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public class SourceServiceTest {
    @Mock
    private NewsSourceRepository sourceRepository;
    @InjectMocks
    private SourceService sourceService;
    @Test
    public void addNewsSource() {
        when(sourceRepository.existsByNameIgnoreCase("Habr")).thenReturn(true);
        // sourceService.addNewSource("")
    }
}
