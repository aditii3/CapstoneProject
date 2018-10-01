package com.example.android.myreddits.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.myreddits.R;
import com.example.android.myreddits.adapter.SearchAdapter;
import com.example.android.myreddits.analytics.Analytics;
import com.example.android.myreddits.data.RedditContract;
import com.example.android.myreddits.utils.RedditRestClient;
import com.example.android.myreddits.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchSubredditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.search_subreddit_toolbar)
    Toolbar toolbar;
    private SearchView searchView;
    @BindView(R.id.swipe_refresh_subscribe)
    ProgressBar bar;
    @BindView(R.id.search_results_lv)
    ListView searchResultsList;
    SearchAdapter searchAdapter;
    private int SEARCH_LOADER = 1;
    private RedditRestClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_subreddit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        searchAdapter = new SearchAdapter(this, null, 0);
        searchResultsList.setAdapter(searchAdapter);
        getSupportLoaderManager().initLoader(SEARCH_LOADER, null, this);
        client = new RedditRestClient(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(onQueryTextListener);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return true;
            }
        });
        return true;
    }


    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            if (query == null || query.length() == 0) return false;

            if (Util.isNetworkAvailable(SearchSubredditActivity.this)) {
                bar.setVisibility(View.VISIBLE);
                client.searchSubredditName(query, new RedditRestClient.Refresher() {
                    @Override
                    public void start() {
                    }

                    @Override
                    public void end() {
                    }
                });

                Analytics.logUserSearch(SearchSubredditActivity.this, query);

            } else {
                Toast.makeText(SearchSubredditActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        bar.setVisibility(View.VISIBLE);
        return new CursorLoader(this, RedditContract.SearchEntry.CONTENT_URI, null, null, null, null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        bar.setVisibility(View.GONE);
        searchAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        searchAdapter.swapCursor(null);
    }

}
