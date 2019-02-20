package com.rokomari.newsviews.repository.remote;

import android.util.Log;

import com.rokomari.newsviews.Config;
import com.rokomari.newsviews.repository.Responses;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteRepo {

    private static final RemoteRepo ourInstance = new RemoteRepo();
    private WebServices retrofitApiInterface;

    public static RemoteRepo getInstance() {
        return ourInstance;
    }

    private RemoteRepo() {
        retrofitApiInterface = RetrofitApiClient.getClient().create(WebServices.class);
    }

    public void getNewsArticles(ResponseCallback callback) {
        retrofitApiInterface.getNewsList(Config.NEWS_API).enqueue(new Callback<Responses.Articles>() {

            @Override
            public void onResponse(Call<Responses.Articles> call, Response<Responses.Articles> response) {

                Log.d("MyApp", "Network layer. User articles Raw response: " + response.raw());

                Responses.Articles data = response.body();
                if (data != null && data.status.equals("ok")) callback.onSuccess(data.articles);
                else callback.onError(new Exception(response.message()));
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}

