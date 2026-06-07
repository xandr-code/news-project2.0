package com.example.news_project.entity;

public enum NewsCategory {
    POLITICS("Политика"),
    ECONOMY("Экономика"),
    SPORT("Спорт"),
    SCIENCE("Наука"),
    TECHNOLOGY("Технологии"),
    CULTURE("Культура"),
    INCIDENTS("Происшествия"),
    SOCIETY("Общество"),
    OTHER("Другое");

    private final String displayName;

    NewsCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
