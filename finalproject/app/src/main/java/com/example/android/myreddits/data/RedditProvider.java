package com.example.android.myreddits.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.myreddits.data.RedditContract.FavsEntry;
import com.example.android.myreddits.data.RedditContract.PostEntry;
import com.example.android.myreddits.data.RedditContract.SearchEntry;


/**
 * Created by aditi on 9/4/2018.
 */

public class RedditProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RedditDbHelper helper;


    static final int FAV = 100;
    static final int FAV_WITH_ID = 101;
    static final int SEARCH = 300;
    static final int SEARCH_WITH_ID = 301;
    static final int POST = 400;
    static final int POST_WITH_ID = 401;
    static final int COMMENT = 500;
    static final int COMMENT_WITH_ID = 501;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RedditContract.AUTHORITY;

        matcher.addURI(authority, RedditContract.FAVS_PATH, FAV);
        matcher.addURI(authority, RedditContract.FAVS_PATH + "/*", FAV);
        matcher.addURI(authority, RedditContract.FAVS_PATH + "/#", FAV_WITH_ID);
        matcher.addURI(authority, RedditContract.COMMENTS_PATH, COMMENT);
        matcher.addURI(authority, RedditContract.COMMENTS_PATH + "/#", COMMENT_WITH_ID);
        matcher.addURI(authority, RedditContract.SEARCH_PATH, SEARCH);
        matcher.addURI(authority, RedditContract.SEARCH_PATH + "/*", SEARCH);
        matcher.addURI(authority, RedditContract.SEARCH_PATH + "/#", SEARCH_WITH_ID);
        matcher.addURI(authority, RedditContract.POSTS_PATH, POST);
        matcher.addURI(authority, RedditContract.POSTS_PATH + "/*", POST);
        matcher.addURI(authority, RedditContract.POSTS_PATH + "/#", POST_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        helper = new RedditDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = helper.getReadableDatabase();
        Cursor result;
        switch (sUriMatcher.match(uri)) {
            case FAV:
                result = db.query(FavsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SEARCH:
                result = db.query(SearchEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case COMMENT:
                result = db.query(RedditContract.CommentEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case POST:
                result = db.query(PostEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri for querying " + uri);


        }
        if (getContext() != null)
            result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FAV:
                return FavsEntry.CONTENT_TYPE;
            case FAV_WITH_ID:
                return FavsEntry.CONTENT_ITEM_TYPE;
            case COMMENT:
                return RedditContract.CommentEntry.CONTENT_TYPE;
            case COMMENT_WITH_ID:
                return RedditContract.CommentEntry.CONTENT_ITEM_TYPE;
            case POST:
                return PostEntry.CONTENT_TYPE;
            case POST_WITH_ID:
                return PostEntry.CONTENT_ITEM_TYPE;
            case SEARCH:
                return SearchEntry.CONTENT_TYPE;
            case SEARCH_WITH_ID:
                return SearchEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Uri insertUri;
        switch (sUriMatcher.match(uri)) {
            case FAV: {
                long _id = db.insert(FavsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    insertUri = FavsEntry.buildUriWithRowId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row " + uri);
                }
                break;
            }
            case COMMENT: {
                long _id = db.insert(RedditContract.CommentEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    insertUri = RedditContract.CommentEntry.buildUriWithRowId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row " + uri);
                }
                break;
            }
            case POST: {
                long _id = db.insert(PostEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    insertUri = PostEntry.buildUriWithRowId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row " + uri);
                }
                break;
            }

            case SEARCH: {
                long _id = db.insert(SearchEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    insertUri = SearchEntry.buildUriWithRowId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unsupported Uri for insertion " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return insertUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        int rows;
        switch (sUriMatcher.match(uri)) {
            case FAV:
                rows = db.delete(FavsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POST:
                rows = db.delete(PostEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SEARCH:
                rows = db.delete(SearchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COMMENT:
                rows = db.delete(RedditContract.CommentEntry.TABLE_NAME, selection, selectionArgs);
                break;


            default:
                throw new UnsupportedOperationException("Unsupported Uri for deletion " + uri);
        }
        if (rows != 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows;
        switch (sUriMatcher.match(uri)) {
            case FAV:
                rows = db.update(FavsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case POST:
                rows = db.update(PostEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SEARCH:
                rows = db.update(SearchEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COMMENT:
                rows = db.update(RedditContract.CommentEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri for updation " + uri);
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public void shutdown() {
        helper.close();
        super.shutdown();
    }
}
