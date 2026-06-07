package com.example.news_project.modelDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewsCandidate {
    String title;
    String Url;
    String description;
    String mediaUrl;
    String content;
    LocalDateTime publishedAt;

}
