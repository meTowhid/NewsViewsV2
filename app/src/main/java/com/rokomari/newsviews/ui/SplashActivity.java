package com.rokomari.newsviews.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.rokomari.newsviews.R;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));

            SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
            boolean isFirstLaunch = pref.getBoolean("isFirstLaunch", true);
            if (isFirstLaunch) {
                pref.edit().putBoolean("isFirstLaunch", false).apply();
                SplashActivity.this.startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            }
            SplashActivity.this.finish();
        }, 1000);
    }

}
