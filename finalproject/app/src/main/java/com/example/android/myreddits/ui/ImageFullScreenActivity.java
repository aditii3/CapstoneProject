package com.example.android.myreddits.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.android.myreddits.R;
import com.example.android.myreddits.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class ImageFullScreenActivity extends AppCompatActivity {

    @BindView(R.id.img_fullscr)
    ImageView image;
    @BindView(R.id.controls)
    LinearLayout close;

    private boolean isVisible;
    private static final int DELAY = 200;
    private final Handler hideHandler = new Handler();


    private final Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.show();

            close.setVisibility(View.VISIBLE);
        }
    };
    private final Runnable handleVisibility = new Runnable() {
        @Override
        public void run() {
            image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    };

    private final Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        ButterKnife.bind(this);
        isVisible = true;
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(Constants.IMG_URL);

            Glide.with(ImageFullScreenActivity.this)
                    .load(url)
                    .into(image);

        }
    }

    @OnTouch(R.id.close_fullscr)
    boolean finishActivity() {
        finish();
        return false;
    }

    @OnClick(R.id.img_fullscr)
    void toggleVisibility() {
        toggle();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (isVisible) hide();
        else show();

    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        close.setVisibility(View.GONE);
        isVisible = false;

        hideHandler.removeCallbacks(showRunnable);
        hideHandler.postDelayed(handleVisibility, DELAY);
    }

    private void show() {

        image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        isVisible = true;

        hideHandler.removeCallbacks(handleVisibility);
        hideHandler.postDelayed(showRunnable, DELAY);
    }

    private void delayedHide() {
        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(hideRunnable, 100);
    }
}
