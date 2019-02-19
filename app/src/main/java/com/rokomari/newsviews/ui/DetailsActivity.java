package com.rokomari.newsviews.ui;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rokomari.newsviews.Cons;
import com.rokomari.newsviews.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        String url = getIntent().getStringExtra(Cons.article);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
