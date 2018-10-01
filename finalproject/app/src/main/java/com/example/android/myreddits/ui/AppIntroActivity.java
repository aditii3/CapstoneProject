package com.example.android.myreddits.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.myreddits.R;
import com.example.android.myreddits.adapter.IntroPagerAdapter;
import com.example.android.myreddits.utils.IntroManager;

public class AppIntroActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Button skipBtn, nextBtn;
    private IntroPagerAdapter adapter;
    private LinearLayout linearLayout;
    private TextView[] dotsArray;

    private IntroManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new IntroManager(this);
        if (!manager.isFirstLaunch()) {
            startSelectionActivty();
            finish();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_app_intro);
        viewPager = findViewById(R.id.view_pager);
        linearLayout = findViewById(R.id.layoutDots);
        skipBtn = findViewById(R.id.btn_skip);
        nextBtn = findViewById(R.id.btn_next);
        addBottomDots(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        adapter = new IntroPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                if (position == IntroPagerAdapter.getIdCount() - 1) {
                    nextBtn.setText(getString(R.string.start));
                    skipBtn.setVisibility(View.GONE);
                } else {
                    nextBtn.setText(getString(R.string.next));
                    skipBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectionActivty();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem() + 1;
                if (current < IntroPagerAdapter.getIdCount()) {
                    viewPager.setCurrentItem(current);
                } else {
                    startSelectionActivty();
                }
            }
        });
    }

    private void startSelectionActivty() {
        manager.setFirstLaunch(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void addBottomDots(int currentPage) {
        dotsArray = new TextView[IntroPagerAdapter.getIdCount()];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        linearLayout.removeAllViews();
        for (int i = 0; i < dotsArray.length; i++) {
            dotsArray[i] = new TextView(this);
            dotsArray[i].setText(Html.fromHtml("&#8226;"));
            dotsArray[i].setTextSize(35);
            dotsArray[i].setTextColor(colorsInactive[currentPage]);
            linearLayout.addView(dotsArray[i]);
        }

        if (dotsArray.length > 0)
            dotsArray[currentPage].setTextColor(colorsActive[currentPage]);
    }
}
