package com.beeminder.gtbee.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.beeminder.gtbee.Utility;
import com.beeminder.gtbee.data.Contract;

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

        Cursor cur = getContentResolver().query(Contract.ACTIVE_TASKS_URI, null,
                Contract.KEY_TITLE + "=\"" + title + "\"", null, null);
        cur.moveToFirst();
        int base_id = cur.getInt(0);
        getContentResolver().delete(Contract.ACTIVE_TASKS_URI, Contract.KEY_ID + "=" + base_id, null);

    }
}
