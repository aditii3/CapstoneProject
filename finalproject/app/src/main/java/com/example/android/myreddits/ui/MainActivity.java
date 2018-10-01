package com.example.android.myreddits.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myreddits.remote.PostsJob;
import com.example.android.myreddits.R;
import com.example.android.myreddits.adapter.FavSubredditAdapter;
import com.example.android.myreddits.adapter.PostAdapter;
import com.example.android.myreddits.data.RedditContract;
import com.example.android.myreddits.utils.RedditRestClient;
import com.example.android.myreddits.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int POSTS_LOADER = 3;
    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean isPostRefreshing = false;
    private static final int FAV_LOADER = 7;
    private static String currentSubreddit;
    FavSubredditAdapter favAdapter;
    ActionBarDrawerToggle toggle;
    PostAdapter postAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.drawer_user_subscribed_list)
    ListView userFavList;
    @BindView(R.id.post_lv)
    ListView postList;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @Nullable
    @BindView(R.id.user_header_desc_tv)
    TextView userDesc;
    @BindView(R.id.swipe_refresh_posts)
    SwipeRefreshLayout swipeResfreshPosts;
    RedditRestClient redditRestClient;


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("current", Context.MODE_PRIVATE);
        currentSubreddit = sharedPreferences.getString("selected", "art");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        redditRestClient = new RedditRestClient(this);
        postAdapter = new PostAdapter(this, null, 0);
        favAdapter = new FavSubredditAdapter(this, null, 0);
        if (userFavList != null)
            userFavList.setAdapter(favAdapter);
        postList.setAdapter(postAdapter);


        SharedPreferences sharedPreferences = getSharedPreferences("current", Context.MODE_PRIVATE);
        currentSubreddit = sharedPreferences.getString("selected", "art");
        getSupportLoaderManager().initLoader(FAV_LOADER, null, this);
        getSupportLoaderManager().initLoader(POSTS_LOADER, null, this);


        swipeResfreshPosts.setColorSchemeColors(getResources().getColor(R.color.bg_screen1), getResources().getColor(R.color.bg_screen2), getResources().getColor(R.color.bg_screen3));
        swipeResfreshPosts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences sharedPreferences = getSharedPreferences("current", Context.MODE_PRIVATE);
                String s = sharedPreferences.getString("selected", "news");
                if (Util.isNetworkAvailable(MainActivity.this)) {
                    redditRestClient.getSubredditPosts(s);
                    refreshPosts();
                } else {
                    swipeResfreshPosts.setRefreshing(false);
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                refreshPosts();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        PostsJob.initialize(this);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (currentSubreddit != null) {
            outState.putString("current", currentSubreddit);
        }
        super.onSaveInstanceState(outState);
    }


    @OnClick(R.id.fab)
    void startSearchActivity() {
        startActivity(new Intent(this, SearchSubredditActivity.class));
    }

    @OnItemClick(R.id.drawer_user_subscribed_list)
    void setSelected(int position) {
        favAdapter.getItem(position);
    }

    private void refreshPosts() {
        isPostRefreshing = true;
        setPostRefreshing();
        getSupportLoaderManager().restartLoader(POSTS_LOADER, null, MainActivity.this);

    }


    private void setPostRefreshing() {
        swipeResfreshPosts.setRefreshing(isPostRefreshing);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case FAV_LOADER:
                return new CursorLoader(this,
                        RedditContract.FavsEntry.CONTENT_URI,
                        null, null, null, null);
            case POSTS_LOADER:
                isPostRefreshing = true;
                setPostRefreshing();
                SharedPreferences sharedPreferences = getSharedPreferences("current", Context.MODE_PRIVATE);
                String s = sharedPreferences.getString("selected", "art");
                return new CursorLoader(this, RedditContract.PostEntry.CONTENT_URI, null, RedditContract.PostEntry.COLUMN_SUBREDDIT_NAME + "=?", new String[]{s}, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case FAV_LOADER:
                favAdapter.swapCursor(data);
                break;
            case POSTS_LOADER:
                postAdapter.swapCursor(data);
                isPostRefreshing = false;
                setPostRefreshing();
                break;
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        swipeResfreshPosts.setRefreshing(true);
        int id = loader.getId();
        switch (id) {
            case FAV_LOADER:
                favAdapter.swapCursor(null);
                break;
            case POSTS_LOADER:
                postAdapter.swapCursor(null);
        }

    }


}
