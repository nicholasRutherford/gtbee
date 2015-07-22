package com.beeminder.gtbee.data;

import android.net.Uri;

/**
 * Created by nick on 17/07/15.
 */
public class TaskContract {

    public static final String CONTENT_AUTHORITY = "com.beeminder.gtbee.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String ACTIVE_TASKS = "/active_tasks";
    public static final String NETWORK_PENDING = "/network_pending";
    public static final String COMPLETED_TASKS = "/completed_tasks";
}
