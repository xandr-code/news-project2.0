package com.example.news_project.modelDto;

public class ImportResult {
    String sourceName;
    int featured;
    int added;
    int dublicates;
    String message;

    public ImportResult(String sourceName, int featured, int added, int dublicates, String message) {
        this.sourceName = sourceName;
        this.featured = featured;
        this.added = added;
        this.dublicates = dublicates;
        this.message = message;
    }

    public ImportResult() {
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getFeatured() {
        return featured;
    }

    public int getAdded() {
        return added;
    }

    public int getDublicates() {
        return dublicates;
    }

    public String getMessage() {
        return message;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public void setAdded(int added) {
        this.added = added;
    }

    public void setDublicates(int dublicates) {
        this.dublicates = dublicates;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
