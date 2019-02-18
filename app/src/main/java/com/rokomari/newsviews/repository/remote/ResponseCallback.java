package com.rokomari.newsviews.repository.remote;

public interface ResponseCallback<T> {
    void onSuccess(T data);
    void onError(Throwable th);
}
