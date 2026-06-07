package com.example.news_project.config;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class UrlNormalizer {

    public String normalize(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return "";
        }
        try {
            URI uri = URI.create(rawUrl.trim());
            String query = uri.getQuery();
            String cleanQuery = query == null ? null : Arrays.stream(query.split("&"))
                    .filter(parameter -> !parameter.toLowerCase().startsWith("utm_"))
                    .filter(parameter -> !parameter.toLowerCase().startsWith("yclid="))
                    .filter(parameter -> !parameter.toLowerCase().startsWith("fbclid="))
                    .collect(Collectors.joining("&"));
            if (cleanQuery != null && cleanQuery.isBlank()) {
                cleanQuery = null;
            }
            return new URI(
                    uri.getScheme(),
                    uri.getUserInfo(),
                    uri.getHost(),
                    uri.getPort(),
                    uri.getPath(),
                    cleanQuery,
                    null
            ).toString();
        } catch (Exception ignored) {
            return rawUrl.trim();
        }
    }
}