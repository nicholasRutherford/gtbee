package com.beeminder.gtbee;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.beeminder.gtbee.data.TaskContract;
import com.beeminder.gtbee.data.TaskDbHelper;
import com.beeminder.gtbee.services.PaymentService;
import com.beeminder.gtbee.services.ReminderService;

import java.util.Calendar;


public class TaskDetail extends ActionBarActivity {
    public String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(TaskFragment.EXTRA_MESSAGE);

        SQLiteDatabase db = new TaskDbHelper(this).getWritableDatabase();
        Cursor cur = db.query(TaskContract.TaskEntry.TABLE_NAME,
                null,
                "title=\"" + mTitle + "\"",
                null, null, null, null);

        if (cur.getCount()==0){
            finish();
        } else {
            cur.moveToFirst();

            Long dateDue = cur.getLong(TaskDbHelper.COL_DUE_DATE);
            Long currentDate = Calendar.getInstance().getTimeInMillis();

            if ((dateDue - currentDate) < 0){
                setContentView(R.layout.activity_task_detail_past);
            } else{
                setContentView(R.layout.activity_task_detail);
            }

            getSupportActionBar().setTitle(mTitle);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View view){
        new Utility().deleteTaskFromTitle(mTitle, this);
        finish();
    }
 /*
    public void deleteTaskFromTitle(String title, Context context){

        String where = TaskContract.TaskEntry.COLUMN_TITLE +"=\""+ title +"\";";


        SQLiteDatabase db = new TaskDbHelper(context).getWritableDatabase();
        Cursor cur = db.query(TaskContract.TaskEntry.TABLE_NAME,
                new String[]{"_ID"},
                "title=\"" + title + "\"",
                null, null, null, null);
        cur.moveToFirst();
        int base_id = cur.getInt(0);
        int hour_id = base_id * 100 + 60; // 60 min in an hour
        int day_id = base_id * 100 + 24; // 24 hours in a day
        int pay_id = base_id * 100 + 55; // 55 = $$

        // Clear current notifications
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(day_id);
        mNotifyMgr.cancel(hour_id);

        Intent nullIntent = new Intent(this, NewTask.class);

        // Remove notification alarms
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentHour = new Intent(this, ReminderService.class);
        intentHour.putExtra(ReminderService.REMINDER_TITLE, title + "!");
        intentHour.putExtra(ReminderService.REMINDER_TEXT, "Due in less than one hour! Eek!");
        intentHour.putExtra(ReminderService.REMINDER_ID, hour_id);
        intentHour.putExtra(ReminderService.TASK_TITLE, title);
        PendingIntent pendingIntentHour = PendingIntent.getService(this, hour_id, intentHour, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntentHour);

        Intent intentDay = new Intent(this, ReminderService.class);
        intentDay.putExtra(ReminderService.REMINDER_TITLE, title + "!");
        intentDay.putExtra(ReminderService.REMINDER_TEXT, "Due in less than one day.");
        intentDay.putExtra(ReminderService.REMINDER_ID, day_id);
        intentDay.putExtra(ReminderService.TASK_TITLE, title);

        PendingIntent pendingIntentDay = PendingIntent.getService(this, day_id, intentDay, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntentDay);


        // Remove payment alarm
        Intent intentPayment = new Intent(this, PaymentService.class);
        intentPayment.putExtra(PaymentService.TASK_TITLE, title);
        intentPayment.putExtra(PaymentService.TASK_ID, base_id);
        intentPayment.putExtra(PaymentService.ATTEMPT_NUMBER, 0);

        PendingIntent pendingIntentPayment = PendingIntent.getService(this, pay_id, intentPayment, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntentPayment);

        // Remove entry from table
        db.delete(
                TaskContract.TaskEntry.TABLE_NAME,
                where,
                null);
        
        finish();

    }
    */

    public void renewTask(View view){

        SQLiteDatabase db = new TaskDbHelper(this).getWritableDatabase();
        Cursor cur = db.query(TaskContract.TaskEntry.TABLE_NAME,
                null,
                "title=\"" + mTitle + "\"",
                null, null, null, null);
        cur.moveToFirst();
        int retryNumeber = cur.getInt(TaskDbHelper.COL_RETRY);

        Intent intent = new Intent(this, NewTask.class);
        intent.putExtra(NewTask.TASK_NAME, mTitle);
        intent.putExtra(NewTask.RETRY_NUMBER, retryNumeber + 1);
        startActivity(intent);
        finish();

    }
}
