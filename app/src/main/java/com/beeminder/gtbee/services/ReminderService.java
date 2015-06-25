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

/**
 * Created by nick on 18/06/15.
 */
public class ReminderService extends IntentService {
    private static final String mName = "ReminderService";

    public static final String TASK_TITLE = "com.beeminder.gtbee.task_title";
    public static final String REMINDER_TITLE = "com.beeminder.gtbee.reminder_title";
    public static final String REMINDER_TEXT = "com.beeminder.gtbee.reminder_text";
    public static final String REMINDER_ID = "com.beeminder.gtbee.reminder_id";


    public ReminderService() {
        super(mName);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long[] vibPattern = {0,100,100,100,100,500};
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        String task_title = intent.getStringExtra(TASK_TITLE);
        String title = intent.getStringExtra(REMINDER_TITLE);
        String text = intent.getStringExtra(REMINDER_TEXT);
        int notification_id = intent.getIntExtra(REMINDER_ID, 0);
        Log.v("ReminderService", title);
        Log.v("ReminderService", "ID: " + Integer.toString(notification_id));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean notify = sharedPref.getBoolean("notifications_checkbox", true);

        if (notify) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setLights(Color.YELLOW, 500, 500)
                    .setVibrate(vibPattern)
                    .setSound(sound)
                    .setTicker(title);

            // Click notification action
            Intent resultIntent = new Intent(this, TaskDetail.class);
            resultIntent.putExtra(TaskFragment.EXTRA_MESSAGE, task_title);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);

            //Add done button
            Intent doneIntent = new Intent(this, DeleteTaskService.class);
            doneIntent.putExtra(DeleteTaskService.TASK_TITLE, task_title);
            PendingIntent pDoneIntent = PendingIntent.getService(this, notification_id * 10 + 2,
                    doneIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            Log.v("ReminderService", "DeleteTask set for: " + task_title);

            mBuilder.addAction(R.drawable.ic_done_black_24dp, "Done!", pDoneIntent);


            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(notification_id, mBuilder.build());
        }
    }
}
