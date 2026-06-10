package com.example.news_project;

import com.example.news_project.config.KeywordExtract;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeywordExtractTest {
    private final KeywordExtract keywordExtract = new KeywordExtract();

    @Test
    public void extractReturnEmptyString() {
        Assertions.assertThat(keywordExtract.extract(null)).isEmpty();
        Assertions.assertThat(keywordExtract.extract("   ")).isEmpty();
    }

    @Test
    public void extractMostPopularWord(){
        String keyword = keywordExtract.extract("""
                java java java spring spring boot boot boot boot
                """);
        Assertions.assertThat(keyword).isEqualTo("boot, java, spring");
    }
}
