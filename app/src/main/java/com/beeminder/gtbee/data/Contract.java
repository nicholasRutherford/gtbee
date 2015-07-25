package com.beeminder.gtbee.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by nick on 17/07/15.
 */
public class Contract {

    public static final String CONTENT_AUTHORITY = "com.beeminder.gtbee.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACTIVE_TASKS = "active_tasks";
    public static final String PATH_NETWORK_PENDING = "network_pending";
    public static final String PATH_COMPLETED_TASKS = "completed_tasks";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GTBeeDB";

    // Table Names
    public static final String TABLE_ACTIVE_TASKS = "active_tasks";
    public static final String TABLE_OLD_TASKS = "old_tasks";
    public static final String TABLE_NETWORK_PENDING_PAYMENT = "network_pending_payment";
    public static final String TABLE_NETWORK_PENDING_BEEMINDER_INT = "network_pending_beeminder_int";

    // Common column names
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DUE_DATE = "due_date";
    public static final String KEY_ADDED_DATE = "added_date";
    public static final String KEY_PENALTY = "penalty";
    public static final String KEY_DESCRIPTION = "description";

    // ACTIVE_TASKS column names


    // OLD_TASKS column names
    public static final String KEY_DONE_DATE = "done_date";

    // NETWORK_PENDING_PAYMENT column names
    public static final String KEY_PAYMENT_STATUS = "payment_status";

    // NETWORK_PENDING_BEEMINDER_INT column names
    public static final String KEY_SENT_STATUS = "sent_status";

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY+ "/" + PATH_ACTIVE_TASKS;

    public static final Uri ACTIVE_TASKS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVE_TASKS).build();
    public static Uri buildTaskUri(long id){
        return ContentUris.withAppendedId(ACTIVE_TASKS_URI, id);
    }
}
