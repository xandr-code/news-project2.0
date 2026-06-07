package com.example.news_project.config;

import org.springframework.stereotype.Component;

@Component
public class SummaryService {

    private static final int MAX_LENGTH = 280;

    public String summarize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String normalized = text.replaceAll("\\s+", " ").trim();
        String[] sentences = normalized.split("(?<=[.!?])\\s+");
        StringBuilder summary = new StringBuilder();
        for (String sentence : sentences) {
            if (summary.length() + sentence.length() > MAX_LENGTH) {
                break;
            }
            if (!summary.isEmpty()) {
                summary.append(' ');
            }
            summary.append(sentence);
            if (summary.length() >= 160) {
                break;
            }
        }
        if (!summary.isEmpty()) {
            return summary.toString();
        }
        return normalized.length() <= MAX_LENGTH ? normalized : normalized.substring(0, MAX_LENGTH - 3) + "...";
    }
}