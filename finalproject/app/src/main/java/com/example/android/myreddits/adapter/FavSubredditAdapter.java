package com.example.android.myreddits.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.myreddits.R;
import com.example.android.myreddits.data.RedditContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditi on 9/5/2018.
 */

public class FavSubredditAdapter extends CursorAdapter {

    private SharedPreferences sharedPreferences;


    public FavSubredditAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        sharedPreferences = context.getSharedPreferences("current", Context.MODE_PRIVATE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        FavViewHolder holder;
        View v = LayoutInflater.from(context).inflate(R.layout.fav_list, parent, false);
        holder = new FavViewHolder(v);
        v.setTag(holder);
        return v;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final FavViewHolder holder = (FavViewHolder) view.getTag();
        final String s = cursor.getString(cursor.getColumnIndex(RedditContract.FavsEntry.COLUMN_NAME));
        holder.id = cursor.getInt(cursor.getColumnIndex(RedditContract.FavsEntry._ID));
        if (s != null && !s.isEmpty()) {
            holder.favName.setText(s);
        }
        cursor.getPosition();


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selected", holder.favName.getText().toString());
                editor.putInt("pos", holder.id);
                editor.apply();


            }
        });
        holder.favImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getContentResolver().delete(RedditContract.FavsEntry.CONTENT_URI, RedditContract.FavsEntry._ID + "=?", new String[]{String.valueOf(holder.id)});
                context.getContentResolver().delete(RedditContract.PostEntry.CONTENT_URI, RedditContract.PostEntry.COLUMN_SUBREDDIT_NAME + "=?", new String[]{holder.favName.getText().toString()});
                context.getContentResolver().notifyChange(RedditContract.FavsEntry.CONTENT_URI, null);
                context.getContentResolver().notifyChange(RedditContract.PostEntry.CONTENT_URI, null);
            }
        });

    }

    static class FavViewHolder {
        @BindView(R.id.fav_img)
        ImageView favImg;
        @BindView(R.id.fav_name_tv)
        TextView favName;
        @BindView(R.id.fav_ll)
        LinearLayout layout;
        int id;

        FavViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

    }
}
