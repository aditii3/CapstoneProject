package com.example.android.myreddits.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.myreddits.R;

/**
 * Created by aditi on 9/4/2018.
 */

public class IntroPagerAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    private static int[] layoutsId = new int[]{
            R.layout.welcome_slide_1,
            R.layout.welcome_slide_2,
            R.layout.welcome_slide_3};
    private final Context context;

    public IntroPagerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutsId[position], container, false);
        container.addView(v);
        return v;
    }

    @Override
    public int getCount() {
        return layoutsId.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View v = (View) object;
        container.removeView(v);
    }

    public static int getIdCount() {
        return layoutsId.length;
    }
}
