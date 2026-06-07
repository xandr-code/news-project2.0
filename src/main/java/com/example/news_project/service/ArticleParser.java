package com.example.news_project.service;

import com.example.news_project.entity.NewsSource;
import com.example.news_project.modelDto.NewsCandidate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleParser {

    public List<NewsCandidate> fetch(NewsSource source) throws IOException {
        Document document = Jsoup.connect(source.getUrl())
                .userAgent("Mozilla/5.0 NewsAggregatorBot/1.0")
                .timeout(12_000)
                .ignoreContentType(true)
                .parser(Parser.xmlParser())
                .get();

        Elements feedItems = document.select("item, entry");
        if (!feedItems.isEmpty()) {
            return parseFeed(feedItems, source.getUrl());
        }
        return List.of(parseHtmlPage(source.getUrl()));
    }

    private List<NewsCandidate> parseFeed(Elements items, String sourceUrl) {
        List<NewsCandidate> candidates = new ArrayList<>();
        for (Element item : items) {
            String title = text(item, "title");
            String url = firstNotBlank(attribute(item, "link[href]", "href"), text(item, "link"), text(item, "guid"));
            String description = firstNotBlank(text(item, "description"), text(item, "summary"));
            String content = firstNotBlank(text(item, "content|encoded"), text(item, "content"), description);
            String mediaUrl = firstNotBlank(attribute(item, "enclosure[url]", "url"), attribute(item, "media|thumbnail[url]", "url"), attribute(item, "media|content[url]", "url"));
            LocalDateTime publishedAt = parseDate(firstNotBlank(text(item, "pubDate"), text(item, "published"), text(item, "updated")));
            if (!title.isBlank() && !url.isBlank()) {
                candidates.add(new NewsCandidate(title, absolutize(sourceUrl, url), description, mediaUrl, content, publishedAt));
            }
        }
        return candidates;
    }

    private NewsCandidate parseHtmlPage(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 NewsAggregatorBot/1.0")
                .timeout(12_000)
                .get();
        String title = firstNotBlank(document.select("meta[property=og:title]").attr("content"), document.title());
        String description = firstNotBlank(document.select("meta[name=description]").attr("content"), document.select("meta[property=og:description]").attr("content"));
        String canonical = firstNotBlank(document.select("link[rel=canonical]").attr("href"), url);
        String mediaUrl = document.select("meta[property=og:image]").attr("content");
        String content = firstNotBlank(document.select("article").text(), document.body() == null ? "" : document.body().text());
        return new NewsCandidate(title, canonical, description, mediaUrl, content, LocalDateTime.now());
    }

    private String text(Element element, String selector) {
        Element selected = element.select(selector).first();
        return selected == null ? "" : selected.text().trim();
    }

    private String attribute(Element element, String selector, String attribute) {
        Element selected = element.select(selector).first();
        return selected == null ? "" : selected.attr(attribute).trim();
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private LocalDateTime parseDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return LocalDateTime.now();
        }
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.RFC_1123_DATE_TIME,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME,
                DateTimeFormatter.ISO_DATE_TIME
        );
        for (DateTimeFormatter formatter : formatters) {
            try {
                return OffsetDateTime.parse(rawDate, formatter).toLocalDateTime();
            } catch (Exception ignored) {
            }
        }
        return LocalDateTime.now();
    }

    private String absolutize(String sourceUrl, String url) {
        try {
            return java.net.URI.create(sourceUrl).resolve(url).toString();
        } catch (Exception ignored) {
            return url;
        }
    }
}
