package com.example.news_project;

import com.example.news_project.entity.NewsSource;
import com.example.news_project.repository.NewsSourceRepository;
import com.example.news_project.service.SourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SourceServiceTest {

    @Mock
    private NewsSourceRepository sourceRepository;

    @InjectMocks
    private SourceService service;

    @Test
    void addNewSourceThrowsWhenNameAlreadyExists() {
        when(sourceRepository.existsByNameIgnoreCase("Habr")).thenReturn(true);

        assertThatThrownBy(() -> service.addNewSource("Habr", "https://habr.com/rss"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addNewSourcePersistsNewSource() {
        when(sourceRepository.existsByNameIgnoreCase("Habr")).thenReturn(false);
        when(sourceRepository.save(org.mockito.ArgumentMatchers.any(NewsSource.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NewsSource result = service.addNewSource("Habr", "https://habr.com/rss");

        assertThat(result.getName()).isEqualTo("Habr");
        assertThat(result.getUrl()).isEqualTo("https://habr.com/rss");

        ArgumentCaptor<NewsSource> sourceCaptor = ArgumentCaptor.forClass(NewsSource.class);
        verify(sourceRepository).save(sourceCaptor.capture());
        assertThat(sourceCaptor.getValue().isEnabled()).isTrue();
    }

    @Test
    void findAllDelegatesToRepository() {
        when(sourceRepository.findAll()).thenReturn(List.of(new NewsSource("One", "https://one.test")));

        assertThat(service.findAll()).hasSize(1);

        verify(sourceRepository).findAll();
    }
}
