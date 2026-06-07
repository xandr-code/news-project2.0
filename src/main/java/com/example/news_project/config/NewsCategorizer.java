package com.example.news_project.config;

import com.example.news_project.entity.NewsCategory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class NewsCategorizer {
    Map<NewsCategory, List<String>> mapOfCategory = new EnumMap<>(NewsCategory.class);

    public NewsCategorizer() {
        mapOfCategory.put(NewsCategory.POLITICS, List.of("президент", "выборы", "санкции", "правительство", "депутат"));
        mapOfCategory.put(NewsCategory.ECONOMY, List.of("экономика", "бюджет", "налог", "инфляция", "валюта"));
        mapOfCategory.put(NewsCategory.SPORT, List.of("футбол", "хоккей", "теннис", "олимпиада", "чемпионат"));
        mapOfCategory.put(NewsCategory.TECHNOLOGY, List.of("технологии", "ии", "робот", "программирование", "гаджет"));
        mapOfCategory.put(NewsCategory.SCIENCE, List.of("наука", "исследование", "учёные", "открытие", "космос"));
        mapOfCategory.put(NewsCategory.CULTURE, List.of("кино", "музыка", "выставка", "театр", "книга"));
        mapOfCategory.put(NewsCategory.INCIDENTS, List.of("дтп", "пожар", "катастрофа", "убийство", "авария"));
        mapOfCategory.put(NewsCategory.SOCIETY, List.of("общество", "социальный", "пенсия", "льготы", "мигранты"));
    }

    public NewsCategory detect(String title, String text) {
        String combined = (title + " " + text).toLowerCase();

        for (Map.Entry<NewsCategory, List<String>> entry : mapOfCategory.entrySet()) {
            NewsCategory category = entry.getKey();
            if (category == NewsCategory.OTHER) {
                continue;
            }

            List<String> keywords = entry.getValue();
            for (String keyword : keywords) {
                if (combined.contains(keyword.toLowerCase())) {
                    return category;
                }
            }
        }
        return NewsCategory.OTHER;
    }
}