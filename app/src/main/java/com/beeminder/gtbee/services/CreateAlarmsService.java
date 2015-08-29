package com.beeminder.gtbee.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.beeminder.gtbee.Utility;
import com.beeminder.gtbee.data.Contract;

import java.util.Calendar;


public class CreateAlarmsService extends IntentService {
    private final String LOG_TAG = this.getClass().getSimpleName();


    public CreateAlarmsService() {
        super("CreateAlarmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Cursor cur = getContentResolver().query(Contract.ALARMS_URI, null, null, null, null);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        while (cur.moveToNext()){
            String title = cur.getString(cur.getColumnIndexOrThrow(Contract.KEY_TITLE));
            String description = cur.getString(cur.getColumnIndexOrThrow(Contract.KEY_DESCRIPTION));
            String alarmType = cur.getString(cur.getColumnIndexOrThrow(Contract.KEY_ALARM_TYPE));
            Long time = cur.getLong(cur.getColumnIndexOrThrow(Contract.KEY_ALARM_TIME));
            int taskID = cur.getInt(cur.getColumnIndexOrThrow(Contract.KEY_TASK_ID));

            Long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime > time){
                continue;
            }

            switch (alarmType){
                case Contract.KEY_ALARM_TYPE_NOTIFICATION_ONE_TIME:
                    if (time > currentTime) {

                        Log.v(LOG_TAG, "notification time: " + Long.toString(time));
                        Log.v(LOG_TAG, "taskID: " + Integer.toString(taskID));

                        Intent notification = new Intent(this, ReminderService.class);
                        notification.putExtra(ReminderService.REMINDER_TITLE, title);
                        notification.putExtra(ReminderService.REMINDER_TEXT, description);
                        notification.putExtra(ReminderService.BASE_ID, taskID);

                        PendingIntent pendingIntent = PendingIntent.getService(this,
                                Utility.taskIdToNotificationAlarm(taskID),
                                notification, PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                    }
                    break;
                case Contract.KEY_ALARM_TYPE_NOTIFICATION_ZENO:
                    break;

                case Contract.KEY_ALARM_TYPE_PAYMENT:
                    Intent intentPayment = new Intent(this, OverdueService.class);
                    intentPayment.putExtra(OverdueService.TASK_ID, taskID);
                    PendingIntent pendingIntentPayment = PendingIntent.getService(this,
                            Utility.taskIdToPaymentAlarm(taskID), intentPayment,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntentPayment);
                    Log.v("newTask", "Payment set for: " + new Utility().niceDateTime(time));
                    break;
            }
        }
    }
}
