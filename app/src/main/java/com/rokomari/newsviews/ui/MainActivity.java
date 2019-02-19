package com.rokomari.newsviews.ui;

import android.os.Bundle;
import android.widget.Toast;

import com.rokomari.newsviews.R;
import com.rokomari.newsviews.repository.entity.Article;
import com.rokomari.newsviews.repository.remote.RemoteRepo;
import com.rokomari.newsviews.repository.remote.ResponseCallback;
import com.rokomari.newsviews.ui.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recycler;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new RecyclerViewAdapter(this, new ArrayList<>());
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::refreshData);
        swipeRefresh.setRefreshing(true);
        refreshData();
    }

    private void refreshData() {
        RemoteRepo.getInstance().getNewsArticles(new ResponseCallback() {
            @Override
            public void onSuccess(Object data) {
                adapter.data = (List<Article>) data;
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable th) {
                Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
