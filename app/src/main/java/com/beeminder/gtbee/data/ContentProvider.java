package com.beeminder.gtbee.data;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.beeminder.gtbee.Utility;
import com.beeminder.gtbee.services.OverdueService;
import com.beeminder.gtbee.services.ReminderService;

/**
 * Created by nick on 22/07/15.
 */

public class ContentProvider extends android.content.ContentProvider {
    public final String LOG_TAG = this.getClass().getSimpleName();
    private static final int ACTIVE_TASKS = 1;
    private static final int ACTIVE_TASK = 2;
    private static final int COMPLETED_TASKS = 3;
    private static final int NETWORK_PENDING = 4;
    private static final int FAILED_TASKS = 5;
    private static final int NETWORK_PENDING_BEEMINDER_INT = 6;
    private static final int ALARMS = 7;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_ACTIVE_TASKS, ACTIVE_TASKS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_ACTIVE_TASKS + "/#", ACTIVE_TASK);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_COMPLETED_TASKS, COMPLETED_TASKS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "network_pending", NETWORK_PENDING);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_FAILED_TASKS, FAILED_TASKS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_NETWORK_PENDING_BEEMINDER_INT, NETWORK_PENDING_BEEMINDER_INT);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_ALARMS, ALARMS);
    }

    private DbHelper mDbHelper;




    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,                // The content URI of the table
                        String[] projection,    // The columns to return for each row
                        String selection,       // Selection criteria
                        String[] selectionArgs, // Selection criteria
                        String sortOrder) {     // The sort order for the returned rows

        Cursor cursor = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case ACTIVE_TASKS:
                cursor = db.query(Contract.TABLE_ACTIVE_TASKS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ACTIVE_TASK:
                if (selection == null){
                    selection = Contract.KEY_ID + "=" + uri.getLastPathSegment();
                } else {
                    selection = selection + Contract.KEY_ID + "=" + uri.getLastPathSegment();
                }

                cursor = db.query(Contract.TABLE_ACTIVE_TASKS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case COMPLETED_TASKS:
                break;
            case NETWORK_PENDING:
                break;
            case FAILED_TASKS:
                cursor = db.query(Contract.TABLE_FAILED_TASKS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NETWORK_PENDING_BEEMINDER_INT:
                break;
            case ALARMS:
                cursor = db.query(Contract.TABLE_ALARMS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.e(LOG_TAG, "Query: Did not match any URIs for: " + uri.toString());
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case ACTIVE_TASKS:
                return Contract.CONTENT_TYPE;
            case COMPLETED_TASKS:
                break;
            case NETWORK_PENDING:
                break;
            case FAILED_TASKS:
                break;
            case NETWORK_PENDING_BEEMINDER_INT:
                break;
            default:
                Log.e(LOG_TAG, "GetType: Did not match any URIs for: " + uri.toString());
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Uri returnUri = null;
        long id;
        switch (sUriMatcher.match(uri)) {
            case ACTIVE_TASKS:
                id = db.insert(Contract.TABLE_ACTIVE_TASKS, null, values);
                if (id > 0){
                    returnUri = Contract.buildTaskUri(id);
                } else {
                    Log.e(LOG_TAG, "Failed to insert row into " + uri);
                }
                break;

            case COMPLETED_TASKS:
                break;

            case NETWORK_PENDING:
                break;

            case FAILED_TASKS:
                id = db.insert(Contract.TABLE_FAILED_TASKS, null, values);
                if (id > 0 ){
                    returnUri = Contract.buildFailedTaskUri(id);
                } else {
                    Log.e(LOG_TAG, "Failed to inert row into " + uri);
                }
                break;

            case NETWORK_PENDING_BEEMINDER_INT:
                break;
            case ALARMS:
                id = db.insert(Contract.TABLE_ALARMS, null, values);
                if (id > 0 ){
                    returnUri = Contract.buildAlarmsUri(id);
                } else {
                    Log.e(LOG_TAG, "Failed to inert row into " + uri);
                }
                break;

            default:
                Log.e(LOG_TAG, "Insert: Did not match any URIs for: " + uri.toString());
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        // Return a content URI for the newly-inserted row
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (sUriMatcher.match(uri)) {
            case ACTIVE_TASKS:
                Cursor cur = db.query(Contract.TABLE_ACTIVE_TASKS, null, selection, selectionArgs, null, null,null, null);
                Context context = getContext();
                cur.moveToFirst();

                int base_id = (int) cur.getLong(cur.getColumnIndexOrThrow(Contract.KEY_ID));


                // Clear current notifications
                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(Utility.taskIdToNotification(base_id));


                // Remove notification alarms
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(context.ALARM_SERVICE);
                Intent intentHour = new Intent(context, ReminderService.class);
                PendingIntent pendingIntentHour = PendingIntent.getService(context, Utility.taskIdToNotificationAlarm(base_id), intentHour, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntentHour);


                // Remove overdue check
                Intent intentPayment = new Intent(context, OverdueService.class);
                intentPayment.putExtra(OverdueService.TASK_ID, base_id);
                PendingIntent pendingIntentPayment = PendingIntent.getService(context, Utility.taskIdToPaymentAlarm(base_id), intentPayment, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntentPayment);

                rowsDeleted = db.delete(Contract.TABLE_ACTIVE_TASKS, selection, selectionArgs);
                break;

            case COMPLETED_TASKS:
                break;
            case NETWORK_PENDING:
                break;
            case FAILED_TASKS:
                break;
            case NETWORK_PENDING_BEEMINDER_INT:
                break;
            case ALARMS:
                rowsDeleted = db.delete(Contract.TABLE_ALARMS, selection, selectionArgs);
            default:
                Log.e(LOG_TAG, "Delete: Did not match any URIs for: " + uri.toString());
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (sUriMatcher.match(uri)) {

            case ACTIVE_TASKS:
                break;
            case COMPLETED_TASKS:
                break;
            case NETWORK_PENDING:
                break;
            case FAILED_TASKS:
                rowsUpdated = db.update(Contract.TABLE_FAILED_TASKS, values, selection, selectionArgs);
                break;
            case NETWORK_PENDING_BEEMINDER_INT:
                rowsUpdated = db.update(Contract.TABLE_NETWORK_PENDING_BEEMINDER_INT, values, selection, selectionArgs);
                break;
            default:
                Log.e(LOG_TAG, "Update: Did not match any URIs for: " + uri.toString());
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows updated
        return rowsUpdated;
    }
}
