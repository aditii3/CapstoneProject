package com.example.android.myreddits.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.android.myreddits.R;
import com.example.android.myreddits.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostWebViewActivity extends AppCompatActivity {
    @BindView(R.id.webv_toolbar)
    Toolbar toolbar;
    @BindView(R.id.webv_post)
    WebView post;
    @BindView(R.id.webv_appBar)
    AppBarLayout appBarLayout;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_post_web_view);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra(Constants.WEB_TITLE);
        url = getIntent().getStringExtra(Constants.WEB_URL);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        post.loadUrl(url);
        toolbar.setTitle(title);
        post.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                for (String s : Constants.VID) {
                    if (request.contains(s)) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(request)));
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });


    }

}
