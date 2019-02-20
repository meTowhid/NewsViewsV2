package com.rokomari.newsviews.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rokomari.newsviews.BuildConfig;
import com.rokomari.newsviews.R;
import com.rokomari.newsviews.repository.entity.Article;
import com.rokomari.newsviews.repository.remote.RemoteRepo;
import com.rokomari.newsviews.repository.remote.ResponseCallback;
import com.rokomari.newsviews.ui.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.recycler_view)
    RecyclerView recycler;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private RecyclerViewAdapter adapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        adapter = new RecyclerViewAdapter(this, new ArrayList<>());
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::refreshData);
        swipeRefresh.setRefreshing(true);
        refreshData();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        name = header.findViewById(R.id.nav_header_name);
        email = header.findViewById(R.id.nav_header_email);
        profile_image = header.findViewById(R.id.nav_header_profile_image);
        loginItem = navigationView.getMenu().findItem(R.id.nav_login);

        auth = FirebaseAuth.getInstance();
        updateUI();
    }

    TextView name;
    TextView email;
    ImageView profile_image;
    MenuItem loginItem;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        updateUI();
    }

    private void updateUI() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());

            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profile_image);
        } else {
            profile_image.setImageResource(R.drawable.img_wizard_2);
            name.setText("Guest user");
            email.setText("");
        }

        loginItem.setTitle(user == null ? "Login" : "Logout");
    }

    RemoteRepo remote = RemoteRepo.getInstance();

    private void refreshData() {
        if (isNetworkAvailable()) remote.getNewsArticles(new ResponseCallback() {
            @Override
            public void onSuccess(Object data) {
                adapter.data = (List<Article>) data;
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable th) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    SearchView searchView;
    MenuItem searchItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(queryTextListener);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        public boolean onQueryTextChange(String newText) {
            return true;
        }

        public boolean onQueryTextSubmit(String query) {
            fetchFromNumbers(query);
            return true;
        }
    };

    public void hideKeyboard() {
        View view = this.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!isAvailable) new AlertDialog.Builder(this)
                .setMessage("Make sure you have an active internet connection and try again..")
                .setPositiveButton("OK", null)
                .show();
        return isAvailable;
    }

    private void fetchFromNumbers(String numberString) {
        String number = "";
        try {
            Integer.parseInt(numberString);
            number = numberString;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
        } finally {
            if (!number.isEmpty() && isNetworkAvailable()) {
                hideKeyboard();
                searchItem.collapseActionView();
                searchView.setQuery("", false);
                searchView.clearFocus();
                String finalNumber = number;
                remote.getNumberMessage(number, new ResponseCallback() {
                    @Override
                    public void onSuccess(Object data) {
                        new AlertDialog.Builder(NavigationActivity.this)
                                .setTitle(finalNumber)
                                .setMessage(data.toString())
                                .setPositiveButton("OK", null)
                                .show();
                    }

                    @Override
                    public void onError(Throwable th) {
                        Toast.makeText(NavigationActivity.this, th.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_about:
                showDialogAbout();
                break;
            case R.id.nav_exit:
                finish();
                break;
            case R.id.nav_login:
                if (auth.getCurrentUser() == null)
                    startActivityForResult(new Intent(this, LoginActivity.class), 11111);
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Logout")
                            .setMessage("Are you sure?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Logout", (a, b) -> {
                                auth.signOut();
                                updateUI();
                            }).show();
                }
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showDialogAbout() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_version)).setText("Version " + BuildConfig.VERSION_NAME);

        dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://github.com/meTowhid/NewsViewsV2"));
            startActivity(i);
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
