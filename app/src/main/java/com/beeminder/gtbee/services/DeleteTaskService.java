package com.beeminder.gtbee.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.beeminder.gtbee.Utility;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in

 */
public class DeleteTaskService extends IntentService {
    public static final String TASK_TITLE = "com.beeminder.gtbee.deletetakservice.task_title";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor
     */
    public DeleteTaskService() {super("DeleteTaskService");}

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getStringExtra(TASK_TITLE);
        Log.v("Delete Task service", "Deleting: " + title);
        new Utility().deleteTaskFromTitle(title, this);
    }
}
