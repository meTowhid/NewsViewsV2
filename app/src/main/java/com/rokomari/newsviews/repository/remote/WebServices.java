package com.rokomari.newsviews.repository.remote;

import com.rokomari.newsviews.repository.Responses;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WebServices {

    @GET
    Call<Responses.Articles> getNewsList(@Url String url);

    @GET
    Call<ResponseBody> getFromNumbersAPI(@Url String url);

}