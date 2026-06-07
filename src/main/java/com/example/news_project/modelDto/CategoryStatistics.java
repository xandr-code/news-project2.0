package com.example.news_project.modelDto;

import com.example.news_project.entity.NewsCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryStatistics {
    NewsCategory newsCategory;
    Long count;

    public NewsCategory getNewsCategory() {
        return newsCategory;
    }

    public Long getCount() {
        return count;
    }

    public void setNewsCategory(NewsCategory newsCategory) {
        this.newsCategory = newsCategory;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
