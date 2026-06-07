package com.example.news_project.config;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class KeywordExtract {
    private static final Set<String> STOP_WORDS = Set.of(
            "это", "как", "или", "для", "что", "при", "над", "под", "без", "его", "она", "они",
            "the", "and", "for", "with", "from", "this", "that", "have", "has", "was", "are"
    );

    public String extract(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        Map<String, Long> words = Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+"))
                .map(String::trim)
                .filter(word -> word.length() > 3)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.groupingBy(word -> word, LinkedHashMap::new, Collectors.counting()));

        return words.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(8)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }
}
