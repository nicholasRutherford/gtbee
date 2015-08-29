package com.beeminder.gtbee.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.beeminder.gtbee.R;
import com.beeminder.gtbee.TaskDetail;
import com.beeminder.gtbee.TaskFragment;
import com.beeminder.gtbee.Utility;
import com.beeminder.gtbee.data.Contract;

/**
 * Created by nick on 18/06/15.
 */
public class ReminderService extends IntentService {
    private static final String mName = "ReminderService";

    public static final String REMINDER_TITLE = "com.beeminder.gtbee.reminder_title";
    public static final String REMINDER_TEXT = "com.beeminder.gtbee.reminder_text";
    public static final String BASE_ID = "com.beeminder.gtbee.reminder_base_id";


    public ReminderService() {
        super(mName);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long[] vibPattern = {0,100,100,100,100,500};
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String title = intent.getStringExtra(REMINDER_TITLE);
        String text = intent.getStringExtra(REMINDER_TEXT);
        long baseID = intent.getIntExtra(BASE_ID, -1);
        int notification_id = Utility.taskIdToNotification(baseID);

        Log.v("ReminderService", title);
        Log.v("ReminderService", "ID: " + Integer.toString(notification_id));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setLights(Color.YELLOW, 500, 500)
                .setVibrate(vibPattern)
                .setSound(sound)
                .setTicker(title);

        // Click notification action
        Intent resultIntent = new Intent(this, TaskDetail.class);
        resultIntent.putExtra(TaskDetail.KEY_ID, baseID);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this, Utility.taskIdToPendingDetailedTask(baseID),
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        //Add done button
        Intent doneIntent = new Intent(this, DeleteTaskService.class);
        doneIntent.putExtra(DeleteTaskService.TASK_ID, baseID);
        PendingIntent pDoneIntent = PendingIntent.getService(this, Utility.taskIdToPendingDone(baseID),
                doneIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.addAction(R.drawable.ic_done_black_24dp, "Done!", pDoneIntent);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notification_id, mBuilder.build());

        getContentResolver().delete(Contract.ALARMS_URI, Contract.KEY_TASK_ID + "=" + baseID, null);
    }

}
