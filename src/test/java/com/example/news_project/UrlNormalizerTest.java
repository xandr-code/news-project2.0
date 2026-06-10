package com.example.news_project;

import com.example.news_project.config.UrlNormalizer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlNormalizerTest {
    UrlNormalizer normalizer = new UrlNormalizer();

    @Test
    public void normalizerBlankUrl() {
        Assertions.assertThat(normalizer.normalize(null)).isEmpty();
        Assertions.assertThat(normalizer.normalize("   ")).isEmpty();

    }

    @Test
    public void normalizerRemoveTrackingParameters() {
        String result = normalizer.normalize(" https://example.com/news?id=42&utm_source=mail&yclid=abc&keep=yes&fbclid=zzz#comments ");
        Assertions.assertThat(result).isEqualTo("https://example.com/news?id=42&keep=yes");
    }
}
