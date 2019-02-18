package com.rokomari.newsviews.repository.entity;

public class Article {
    public Source source;
    public String author, title, description, url, urlToImage, publishedAt, content;

    class Source {
        public String id, name;
    }
}
