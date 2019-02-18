package com.rokomari.newsviews.repository;

import com.rokomari.newsviews.repository.entity.Article;

import java.util.List;

public class Responses {

    static public class Articles {
        public String status;
        public Integer totalResults;
        public List<Article> articles;
    }
}
