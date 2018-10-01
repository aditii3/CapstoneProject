package com.example.android.myreddits.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aditi on 9/4/2018.
 */

public class RedditContract {
    public static final String AUTHORITY = "com.example.android.myreddits";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String FAVS_PATH = "favs";
    public static final String POSTS_PATH = "posts";
    public static final String SEARCH_PATH = "search";
    public static final String COMMENTS_PATH = "comments";
    private static final String SEP = "/";

    public static final class PostEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(POSTS_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + SEP +
                AUTHORITY + "/" + POSTS_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + SEP +
                AUTHORITY + SEP + POSTS_PATH;
        public static final String TABLE_NAME = "posts";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DOMAIN = "domain";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_COMMENT_COUNT = "comment_count";
        public static final String COLUMN_SUBREDDIT_NAME = "subreddit_name";
        public static final String COLUMN_SUBREDDIT_ID = "subreddit_id";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_PERMALINK = "permalink";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_IMG_URL = "img_url";

        public static final Uri buildUriWithRowId(long rowId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(rowId)).build();
        }

        public static Uri buildUriWithSubpath(String subPathName) {
            return CONTENT_URI.buildUpon().appendPath(subPathName).build();
        }
    }


    public static final class SearchEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(SEARCH_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + SEP +
                AUTHORITY + SEP + SEARCH_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + SEP +
                AUTHORITY + SEP + SEARCH_PATH;
        public static final String TABLE_NAME = "search";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESC = "desc";
        public static final String COLUMN_NUM_SUBSCRIBERS = "subscribers";

        public static final Uri buildUriWithRowId(long rowId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(rowId)).build();
        }
    }

    public static final class FavsEntry implements BaseColumns {

        public static final String TABLE_NAME = "favs";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(FAVS_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + SEP +
                AUTHORITY + SEP + FAVS_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + SEP +
                AUTHORITY + SEP + FAVS_PATH;

        public static final Uri buildUriWithRowId(long rowId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(rowId)).build();
        }

    }

    public static final class CommentEntry implements BaseColumns {
        public static final String TABLE_NAME = "comments";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_MAIN = "main";
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(COMMENTS_PATH).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + COMMENTS_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + COMMENTS_PATH;


        public static final Uri buildUriWithRowId(long rowId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(rowId)).build();
        }
    }


}
