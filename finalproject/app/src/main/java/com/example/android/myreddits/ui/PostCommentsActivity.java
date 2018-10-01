package com.example.android.myreddits.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.android.myreddits.R;
import com.example.android.myreddits.data.RedditContract;
import com.example.android.myreddits.utils.Constants;
import com.example.android.myreddits.utils.RedditRestClient;
import com.example.android.myreddits.utils.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostCommentsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RedditRestClient.Refresher {
    private static final String TAG = PostCommentsActivity.class.getSimpleName();
    private static final int DETAILSLOADER = 9;
    private static final int COMMENTSLOADER = 10;
    private String subredditName;
    private int id;
    private Menu menu;
    private int d = 0x000000;
    private static Cursor cursor;
    private static Cursor commentsCursor;
    private String subredditId;
    @BindView(R.id.cmt_toolbar)
    Toolbar toolbar;
    @BindView(R.id.share_fab)
    FloatingActionButton shareFab;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.cmt_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.cmt_author)
    TextView author;
    @BindView(R.id.cmt_comments)
    TextView comments;
    @BindView(R.id.cmt_title)
    TextView title;
    @BindView(R.id.cmt_score)
    TextView score;
    @BindView(R.id.cmt_domain)
    TextView domain;
    @BindView(R.id.post_cmt_img)
    ImageView img;
    @BindView(R.id.cmt_time)
    TextView time;
    @BindView(R.id.comments_lv)
    LinearLayout commentsList;
    RedditRestClient client;
    @BindView(R.id.loading_pb)
    ProgressBar progressBar;
    @BindView(R.id.ad_view)
    AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            final View decor = getWindow().getDecorView();
            if (decor == null)
                return;
            postponeEnterTransition();
            decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onPreDraw() {
                    decor.getViewTreeObserver().removeOnPreDrawListener(this);
                    startPostponedEnterTransition();
                    return true;
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.shared));

        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.transparent));
        Intent i = getIntent();
        if (i != null && i.getAction() != null) {
            if (i.getAction().equals(Constants.SHOW_POSTS)) {
                subredditName = i.getStringExtra(Constants.POST_NAME);
                id = i.getIntExtra(Constants.POST_ID, 0);
            }
            if (i.getAction().equals(Constants.WIDGET_POST)) {
                subredditName = i.getStringExtra(Constants.WIDGET_NAME);
                id = i.getIntExtra(Constants.WIDGET_ID, 0);
            }
            collapsingToolbarLayout.setTitle(subredditName);
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
            collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.transparent));
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        client = new RedditRestClient(this);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean show = false;
            int range = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (range == -1)
                    range = appBarLayout.getTotalScrollRange();
                if (range + verticalOffset == 0) {
                    show = true;
                    showFab(R.id.action_share);
                } else if (show) {
                    show = false;
                    hideFab(R.id.action_share);
                }
            }
        });
        Util.deleteComments(this);
        getSupportLoaderManager().initLoader(DETAILSLOADER, null, this);
        getSupportLoaderManager().initLoader(COMMENTSLOADER, null, this);
        MobileAds.initialize(this, "ca-app-pub-9720097697712748~6759372633");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private void refreshComments() {
        progressBar.setVisibility(View.VISIBLE);
        commentsList.removeAllViews();

    }

    private void getComments() {
        if (Util.isNetworkAvailable(this)) {

            client.getComments(this, subredditName, subredditId, this);
            refreshComments();
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void bindViews() {
        if (cursor == null || cursor.isClosed())
            return;
        if (cursor.moveToFirst()) {
            subredditId = cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_ID));
            getComments();
            domain.setText(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_DOMAIN)));
            title.setText(cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_TITLE)));
            author.setText(String.format(getString(R.string.author), cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_AUTHOR))));
            String imgUrl = cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_IMG_URL));

            score.setText(String.format(getString(R.string.score), cursor.getInt(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_SCORE))));
            comments.setText(String.format(getString(R.string.comments), cursor.getInt(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_COMMENT_COUNT))));
            long t = (long) cursor.getInt(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_CREATED));
            time.setText(Util.getTime(t));

            if (imgUrl != null && !imgUrl.isEmpty()) {

                Glide.with(this)
                        .asBitmap()
                        .load(imgUrl)
                        .apply(RequestOptions.placeholderOf(R.drawable.gray_bg))
                        .apply(RequestOptions.circleCropTransform())
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                Palette.from(resource)
                                        .generate(new Palette.PaletteAsyncListener() {
                                            @Override
                                            public void onGenerated(@NonNull Palette palette) {
                                                Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                                                collapsingToolbarLayout.setBackgroundColor(palette.getLightMutedColor(d));
                                                collapsingToolbarLayout.setContentScrimColor(palette.getLightMutedColor(d));
                                                if (swatch != null) {
                                                    int c = swatch.getRgb();
                                                    title.setTextColor(c);
                                                    comments.setTextColor(c);
                                                    author.setTextColor(c);
                                                    score.setTextColor(c);
                                                    time.setTextColor(c);
                                                    domain.setTextColor(c);

                                                }
                                            }
                                        });

                                return false;

                            }

                        })
                        .into(img);


            }
        }

    }

    @OnClick(R.id.cmt_title)
    void openPostWebView() {
        Intent i = new Intent(this, PostWebViewActivity.class);
        i.putExtra(Constants.WEB_TITLE, title.getText().toString());
        i.putExtra(Constants.WEB_URL, postUrl());
        startActivity(i);

    }

    @OnClick(R.id.post_cmt_img)
    void openFullScreenImage() {
        Intent i = new Intent(this, ImageFullScreenActivity.class);
        if (cursor.getCount() > 0 && !cursor.isClosed()) {
            String postUrl = postUrl();
            if (postUrl != null) {
                for (String val : Constants.VID) {
                    if (postUrl.contains(val)) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl)));
                        finish();
                    }
                }
            }

            String imgUrl = cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_IMG_URL));
            if (imgUrl != null) {
                for (String s : Constants.VID) {
                    if (imgUrl.contains(s)) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl)));
                        finish();
                    }
                }
                i.putExtra(Constants.IMG_URL, imgUrl);
                startActivity(i);
            }
        }
    }

    private String postUrl() {
        if (cursor.isClosed() || !cursor.moveToFirst() || cursor == null)
            return null;
        return cursor.getString(cursor.getColumnIndex(RedditContract.PostEntry.COLUMN_URL));

    }


    private void showFab(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @OnClick(R.id.share_fab)
    void shareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, postUrl() + title.getText().toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    private void hideFab(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                getComments();
                return true;
            case R.id.action_share:
                shareIntent();
                return true;
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.post_menu, menu);
        hideFab(R.id.action_share);

        return true;
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case DETAILSLOADER:
                return new CursorLoader(this, RedditContract.PostEntry.CONTENT_URI, null, RedditContract.PostEntry._ID + "=?", new String[]{String.valueOf(this.id)}, null);
            case COMMENTSLOADER:
                return new CursorLoader(this, RedditContract.CommentEntry.CONTENT_URI, null, null, null, null);
        }
        return null;

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAILSLOADER:
                cursor = data;
                bindViews();
                break;
            case COMMENTSLOADER:
                commentsCursor = data;
                bindComments(commentsCursor, commentsList);
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DETAILSLOADER:
                cursor = null;
                break;
            case COMMENTSLOADER:
                commentsCursor = null;
                break;
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    private void bindComments(Cursor cursor, LinearLayout linearLayaoutComments) {
        try {
            if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
                linearLayaoutComments.removeAllViews();
                progressBar.setVisibility(View.GONE);
                do {

                    View viewComment = LayoutInflater.from(PostCommentsActivity.this).inflate(R.layout.comment_item, null);
                    TextView username = viewComment.findViewById(R.id.comment_username);
                    TextView score = viewComment.findViewById(R.id.comment_score);
                    TextView content = viewComment.findViewById(R.id.comment_content);
                    TextView time = viewComment.findViewById(R.id.comment_time);
                    username.setText(cursor.getString(cursor.getColumnIndex(RedditContract.CommentEntry.COLUMN_AUTHOR)));
                    content.setText(cursor.getString(cursor.getColumnIndex(RedditContract.CommentEntry.COLUMN_CONTENT)));
                    score.setText(String.format(getString(R.string.score), cursor.getInt(cursor.getColumnIndex(RedditContract.CommentEntry.COLUMN_SCORE))));
                    long t = (long) cursor.getInt(cursor.getColumnIndex(RedditContract.CommentEntry.COLUMN_CREATED));
                    time.setText(Util.getTime(t));
                    linearLayaoutComments.addView(viewComment);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}