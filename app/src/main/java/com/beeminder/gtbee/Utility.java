package com.beeminder.gtbee;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.beeminder.gtbee.data.Contract;
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

    public static String formatPenalty(int penalty){
        return "$" + Integer.toString(penalty);
    }

    /* Returns:
        Today
        Tuseday (Day of week For items <7 days away)
        May 5 (Month Day for items >6 days away)
     */
    public static String niceDate(Long time_mili) {
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
    private static String niceDateWeek(Long time_mili){
        Date date = new Date(time_mili);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault());
    }

    private static String niceDateToday(Long time_mili){
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

    private static String niceDateFull(Long time_mili){
        Date date = new Date(time_mili);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));

        return month + " " + day;

    }

    public static String niceTime(Long time_mili){
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

    public static String niceDateTime(Long time_mili){
        String date = niceDate(time_mili);
        String time = niceTime(time_mili);

        return  date + ", " + time;
    }

    public static String mainScreenTop(Long time_mili){
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

    public static String mainScreenBottom(Long time_mili){
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

    public static int retryToAmount(int retryNumber) {
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

    public static String dialogTime(Long dueDate){
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

    public static int taskIdToNotificationAlarm(int TaskID){
        return  TaskID * 100 + 2;
    }

    public static int taskIdToNotification(long TaskID){
        return (int)TaskID * 100 + 3;
    }

    public static int taskIdToPendingDetailedTask(long TaskId){
        return (int)TaskId * 100 + 4;
    }

    public static int taskIdToPendingDone(long TaskId){
        return (int)TaskId * 100 + 5;
    }

    public static int taskIdToPaymentAlarm(long TaskID){
        return (int)TaskID* 100 + 6;
    }
}
