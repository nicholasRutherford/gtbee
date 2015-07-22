package com.beeminder.gtbee.data;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by nick on 17/07/15.
 */
public final class Contract {

    public static final String CONTENT_AUTHORITY = "com.beeminder.gtbee.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACTIVE_TASKS = "active_tasks";
    public static final String PATH_NETWORK_PENDING = "network_pending";
    public static final String PATH_COMPLETED_TASKS = "completed_tasks";

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY+ "/" + PATH_ACTIVE_TASKS;
}
