package com.example.android.myreddits.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.myreddits.R;
import com.example.android.myreddits.data.RedditContract.PostEntry;
import com.example.android.myreddits.ui.PostCommentsActivity;
import com.example.android.myreddits.utils.Constants;
import com.example.android.myreddits.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by aditi on 9/5/2018.
 */

public class PostAdapter extends CursorAdapter {

    public PostAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }


    @Override

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        PostViewHolder holder;
        View v = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        holder = new PostViewHolder(v);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final PostViewHolder holder = (PostViewHolder) view.getTag();
        String imageUrl = cursor.getString(cursor.getColumnIndex(PostEntry.COLUMN_THUMBNAIL));
        long time = (long) cursor.getInt(cursor.getColumnIndex(PostEntry.COLUMN_CREATED));
        holder.postTime.setText(Util.getTime(time));
        holder.id = cursor.getInt(cursor.getColumnIndex(PostEntry._ID));
        holder.postContent.setText(cursor.getString(cursor.getColumnIndex(PostEntry.COLUMN_TITLE)));
        holder.postDomain.setText(cursor.getString(cursor.getColumnIndex(PostEntry.COLUMN_DOMAIN)));
        holder.postAuthor.setText(cursor.getString(cursor.getColumnIndex(PostEntry.COLUMN_AUTHOR)));
        holder.postLink.setText(cursor.getString(cursor.getColumnIndex(PostEntry.COLUMN_SUBREDDIT_NAME)));
        holder.postCmntCount.setText(String.format(context.getString(R.string.comments), cursor.getInt(cursor.getColumnIndex(PostEntry.COLUMN_COMMENT_COUNT))));
        holder.postScore.setText(String.format(context.getString(R.string.score), cursor.getInt(cursor.getColumnIndex(PostEntry.COLUMN_SCORE))));
        holder.postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor != null) {
                    Intent i = new Intent(context, PostCommentsActivity.class);
                    i.putExtra(Constants.POST_NAME, holder.postLink.getText().toString());
                    i.putExtra(Constants.POST_ID, holder.id);
                    i.setAction(Constants.SHOW_POSTS);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, holder.postImage, context.getString(R.string.post_img_transition));
                    context.startActivity(i, options.toBundle());
                }
            }
        });

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.gray_bg))
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.postImage);
        }
    }


    static class PostViewHolder {
        @BindView(R.id.post_author_tv)
        TextView postAuthor;
        @BindView(R.id.post_cmnt_count_tv)
        TextView postCmntCount;
        @BindView(R.id.post_content_tv)
        TextView postContent;
        @BindView(R.id.post_link_tv)
        TextView postLink;
        @BindView(R.id.reddit_post_img)
        ImageView postImage;
        @BindView(R.id.post_time_tv)
        TextView postTime;
        @BindView(R.id.post_domain_tv)
        TextView postDomain;
        @BindView(R.id.post_score_tv)
        TextView postScore;
        @BindView(R.id.post_layout)
        ConstraintLayout postLayout;
        int id;

        PostViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

    }
}

