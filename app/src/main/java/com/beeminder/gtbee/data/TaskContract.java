package com.beeminder.gtbee.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nick on 11/06/15.
 */
public class TaskContract {

    public static final String CONTENT_AUTHORITY = "com.beeminder.gtbee";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns {

        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_ADDED_DATE = "added_date";
        public static final String COLUMN_PENALTY = "penalty";
        public static final String COLUMN_RETRY_COUNT ="retry_count";

    }
}
