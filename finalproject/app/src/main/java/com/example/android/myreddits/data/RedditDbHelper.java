package com.example.android.myreddits.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.myreddits.data.RedditContract.FavsEntry;
import com.example.android.myreddits.data.RedditContract.PostEntry;
import com.example.android.myreddits.data.RedditContract.SearchEntry;

/**
 * Created by aditi on 9/4/2018.
 */

public class RedditDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myreddits.db";
    private static final int DATABASE_VERSION = 1;

    public RedditDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_SEARCH_TABLE = "CREATE TABLE " +
                SearchEntry.TABLE_NAME + " (" +
                SearchEntry._ID + " INTEGER PRIMARY KEY," +
                SearchEntry.COLUMN_NUM_SUBSCRIBERS + " TEXT NOT NULL," +
                SearchEntry.COLUMN_DESC + " TEXT NOT NULL," +
                SearchEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                " UNIQUE (" + SearchEntry.COLUMN_NAME +
                ") ON CONFLICT REPLACE);";

        final String CREATE_FAVS_TABLE = "CREATE TABLE " + FavsEntry.TABLE_NAME
                + " (" +
                FavsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FavsEntry.COLUMN_ID + " TEXT, " +
                " UNIQUE (" + FavsEntry.COLUMN_NAME + ") ON CONFLICT REPLACE" +
                ");";
        final String CREATE_POSTS_TABLE = "CREATE TABLE " + PostEntry.TABLE_NAME + " (" +
                PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PostEntry.COLUMN_ID + " TEXT, " +
                PostEntry.COLUMN_TITLE + " TEXT, " +
                PostEntry.COLUMN_AUTHOR + " TEXT, " +
                PostEntry.COLUMN_SCORE + " INTEGER, " +
                PostEntry.COLUMN_DOMAIN + " TEXT," +
                PostEntry.COLUMN_COMMENT_COUNT + " INTEGER, " +
                PostEntry.COLUMN_SUBREDDIT_NAME + " TEXT, " +
                PostEntry.COLUMN_SUBREDDIT_ID + " TEXT, " +
                PostEntry.COLUMN_CREATED + " INTEGER, " +
                PostEntry.COLUMN_PERMALINK + " TEXT, " +
                PostEntry.COLUMN_IMG_URL + " TEXT, " +
                PostEntry.COLUMN_URL + " TEXT, " +
                PostEntry.COLUMN_THUMBNAIL + " TEXT," +
                " UNIQUE (" + PostEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";
        final String CREATE_COMMENTS_TABLE = "CREATE TABLE " + RedditContract.CommentEntry.TABLE_NAME + " (" +
                RedditContract.CommentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RedditContract.CommentEntry.COLUMN_ID + " TEXT," +
                RedditContract.CommentEntry.COLUMN_MAIN + " TEXT," +
                RedditContract.CommentEntry.COLUMN_AUTHOR + " TEXT," +
                RedditContract.CommentEntry.COLUMN_CONTENT + " TEXT," +
                RedditContract.CommentEntry.COLUMN_SCORE + " TEXT," +
                RedditContract.CommentEntry.COLUMN_CREATED + " INTEGER" +
                ");";

        db.execSQL(CREATE_FAVS_TABLE);
        db.execSQL(CREATE_SEARCH_TABLE);
        db.execSQL(CREATE_POSTS_TABLE);
        db.execSQL(CREATE_COMMENTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + RedditContract.FavsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RedditContract.SearchEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RedditContract.PostEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RedditContract.CommentEntry.TABLE_NAME);
        onCreate(db);
    }
}
