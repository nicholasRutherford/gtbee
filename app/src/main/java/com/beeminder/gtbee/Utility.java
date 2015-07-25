package com.beeminder.gtbee;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beeminder.gtbee.services.ReminderService;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nick on 11/06/15.
 */
public class Utility {
    private static final long sec_mili = 1000l;
    private static final long min_mili = 60*sec_mili;
    private static final long hour_mili = 60*min_mili;
    private static final long day_mili = 24*hour_mili;
    private static final int buffer_time = 1000*30;

    public String formatPenalty(int penalty){
        return "$" + Integer.toString(penalty);
    }

    /* Returns:
        Today
        Tuseday (Day of week For items <7 days away)
        May 5 (Month Day for items >6 days away)
     */
    public String niceDate(Long time_mili) {
        Long current = Calendar.getInstance().getTimeInMillis();

        Date date = new Date(time_mili);

        if (( time_mili- current) < -1*buffer_time){
            // Date is before now, format like normal
            return niceDateFull(time_mili);
        } else if ((time_mili - current)  < day_mili){
            //Check if the day is today
            return niceDateToday(time_mili);

        } else if((time_mili - current)  < 6*day_mili) {
            // Date is this week
            return niceDateWeek(time_mili);

        } else {
            // Further than one week
            return niceDateFull(time_mili);
        }

    }

    // IE Thursday
    private String niceDateWeek(Long time_mili){
        Date date = new Date(time_mili);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault());
    }

    private String niceDateToday(Long time_mili){
        Calendar cur = Calendar.getInstance();

        Date date = new Date(time_mili);
        Calendar given = Calendar.getInstance();
        given.setTime(date);

        if (cur.get(Calendar.DAY_OF_WEEK) == given.get(Calendar.DAY_OF_WEEK)){
            return "Today";
        } else {
            return niceDateWeek(time_mili);
        }
    }

    private String niceDateFull(Long time_mili){
        Date date = new Date(time_mili);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));

        return month + " " + day;

    }

    public String niceTime(Long time_mili){
        Date date = new Date(time_mili);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int rawHour = cal.get(Calendar.HOUR);
        if (rawHour == 0){
            rawHour = 12;
        }

        String hour = Integer.toString(rawHour);
        String min = String.format("%02d", cal.get(Calendar.MINUTE));
        String ampm = cal.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault()).toLowerCase();

        return hour + ":" + min + " " + ampm;
    }

    public String niceDateTime(Long time_mili){
        String date = niceDate(time_mili);
        String time = niceTime(time_mili);

        return  date + ", " + time;
    }

    public String mainScreenTop(Long time_mili){
        Long current = Calendar.getInstance().getTimeInMillis();
        Calendar cur = Calendar.getInstance();

        Date date = new Date(time_mili);
        Calendar given = Calendar.getInstance();
        given.setTime(date);

        if (( time_mili - current) <0){
            return "Over";
        }

        if (cur.get(Calendar.DAY_OF_WEEK) == given.get(Calendar.DAY_OF_WEEK)){
            int rawHour = given.get(Calendar.HOUR);
            if (rawHour == 0){
                rawHour = 12;
            }

            String hour = Integer.toString(rawHour);
            String min = String.format("%02d", given.get(Calendar.MINUTE));

            return hour + ":" + min;
        } else {
            return niceDate(time_mili);
        }

    }

    public String mainScreenBottom(Long time_mili){
        Long current = Calendar.getInstance().getTimeInMillis();
        Calendar cur = Calendar.getInstance();

        Date date = new Date(time_mili);
        Calendar given = Calendar.getInstance();
        given.setTime(date);

        if (( time_mili - current) <0){
            return "Due";
        }

        if (((time_mili - current)  < day_mili )
                && (cur.get(Calendar.DAY_OF_WEEK) == given.get(Calendar.DAY_OF_WEEK))){

            return cur.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault()).toUpperCase();
        } else {
            return niceTime(time_mili);
        }
    }

    public int retryToAmount(int retryNumber) {
        if (retryNumber < 1) {
            return 5;
        } else if (retryNumber == 1) {
            return 10;
        } else if (retryNumber == 2) {
            return 30;
        } else if (retryNumber == 3) {
            return 90;
        } else if (retryNumber == 4) {
            return 270;
        } else if (retryNumber == 5) {
            return 810;
        } else {
            return 2430;
        }
    }

    public String dialogTime(Long dueDate){
        Long currentDate = Calendar.getInstance().getTimeInMillis() - buffer_time;
        Long diff = dueDate - currentDate;
        if (diff < sec_mili){
            return Long.toString(diff) + " miliseconds.";
        } else if (diff <min_mili){
            return Integer.toString((int) Math.floor(diff / sec_mili)) + " seconds.";
        } else if (diff < hour_mili){
            return Integer.toString((int) Math.floor(diff / min_mili)) + " minutes.";
        } else if (diff < day_mili){
            return Integer.toString((int) Math.floor(diff / hour_mili)) + " hours.";
        } else {
            return Integer.toString((int) Math.floor(diff / day_mili)) + " days.";
        }

    }

    public void deleteTaskFromTitle(String title, Context context){

        SQLiteDatabase db = new TaskDbHelper(context).getWritableDatabase();
        Cursor cur = db.query(TaskDbHelper.TABLE_NAME,
                new String[]{"_ID"},
                "title=\"" + title + "\"",
                null, null, null, null);
        cur.moveToFirst();
        int base_id = cur.getInt(0);
        int hour_id = base_id * 100 + 60; // 60 min in an hour
        int day_id = base_id * 100 + 24; // 24 hours in a day
        int pay_id = base_id * 100 + 55; // 55 = $$

        String where = TaskDbHelper.COLUMN_ID +"=\""+ base_id +"\";";

        // Clear current notifications
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(day_id);
        mNotifyMgr.cancel(hour_id);

        Intent nullIntent = new Intent(context, NewTask.class);

        // Remove notification alarms
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intentHour = new Intent(context, ReminderService.class);
        intentHour.putExtra(ReminderService.REMINDER_TITLE, title + "!");
        intentHour.putExtra(ReminderService.REMINDER_TEXT, "Due in less than one hour! Eek!");
        intentHour.putExtra(ReminderService.REMINDER_ID, hour_id);
        intentHour.putExtra(ReminderService.TASK_TITLE, title);
        PendingIntent pendingIntentHour = PendingIntent.getService(context, hour_id, intentHour, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntentHour);

        Intent intentDay = new Intent(context, ReminderService.class);
        intentDay.putExtra(ReminderService.REMINDER_TITLE, title + "!");
        intentDay.putExtra(ReminderService.REMINDER_TEXT, "Due in less than one day.");
        intentDay.putExtra(ReminderService.REMINDER_ID, day_id);
        intentDay.putExtra(ReminderService.TASK_TITLE, title);

        PendingIntent pendingIntentDay = PendingIntent.getService(context, day_id, intentDay, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntentDay);


        // Remove payment alarm
        //TODO
//        Intent intentPayment = new Intent(context, PaymentService.class);
//        intentPayment.putExtra(PaymentService.TASK_TITLE, title);
//        intentPayment.putExtra(PaymentService.TASK_ID, base_id);
//        intentPayment.putExtra(PaymentService.ATTEMPT_NUMBER, 0);

//        PendingIntent pendingIntentPayment = PendingIntent.getService(context, pay_id, intentPayment, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.cancel(pendingIntentPayment);

        // Remove entry from table
        db.delete(
                TaskDbHelper.TABLE_NAME,
                where,
                null);

    }



}
