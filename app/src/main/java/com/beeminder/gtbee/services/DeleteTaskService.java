package com.beeminder.gtbee.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.beeminder.gtbee.Utility;
import com.beeminder.gtbee.data.Contract;


public class DeleteTaskService extends IntentService {
    public static final String TASK_ID = "com.beeminder.gtbee.deletetakservice.task_id";


    public DeleteTaskService() {super("DeleteTaskService");}

    @Override
    protected void onHandleIntent(Intent intent) {
        Long taskID = intent.getLongExtra(TASK_ID, -1l);
        Log.v("Delete Task service", "Deleting: " + Long.toString(taskID));
        getContentResolver().delete(Contract.ACTIVE_TASKS_URI, Contract.KEY_ID + "=" + taskID, null);
    }
}
