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

import com.beeminder.gtbee.services.ReminderService;

/**
 * Created by nick on 22/07/15.
 */

public class ContentProvider extends android.content.ContentProvider {
    public final String LOG_TAG = this.getClass().getSimpleName();
    private static final int ACTIVE_TASKS = 1;
    private static final int OLD_TASKS = 2;
    private static final int NETWORK_PENDING = 3;
    private static final int NETWORK_PENDING_PAYMENT = 4;
    private static final int NETWORK_PENDING_BEEMINDER_INT = 5;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_ACTIVE_TASKS, ACTIVE_TASKS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_OLD_TASKS, OLD_TASKS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "network_pending", NETWORK_PENDING);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_FAILED_TASKS, NETWORK_PENDING_PAYMENT);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_NETWORK_PENDING_BEEMINDER_INT, NETWORK_PENDING_BEEMINDER_INT);
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
                Log.v(LOG_TAG, "Active_Task");
                cursor = db.query(Contract.TABLE_ACTIVE_TASKS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case OLD_TASKS:
                Log.v(LOG_TAG, "Old tasks");
                break;
            case NETWORK_PENDING:
                Log.v(LOG_TAG, "Network pending");
                break;
            case NETWORK_PENDING_PAYMENT:
                Log.v(LOG_TAG, "Network pending payments");
                cursor = db.query(Contract.TABLE_NETWORK_PENDING_PAYMENT, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NETWORK_PENDING_BEEMINDER_INT:
                Log.v(LOG_TAG, "Network prending beeminder int");
                break;
            default:
                Log.e(LOG_TAG, "Did not match any URIs for: " + uri.toString());
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case ACTIVE_TASKS:
                return Contract.CONTENT_TYPE;
            case OLD_TASKS:
                Log.v(LOG_TAG, "Old tasks");
                break;
            case NETWORK_PENDING:
                Log.v(LOG_TAG, "Network pending");
                break;
            case NETWORK_PENDING_PAYMENT:
                Log.v(LOG_TAG, "Network pending payments");
                break;
            case NETWORK_PENDING_BEEMINDER_INT:
                Log.v(LOG_TAG, "Network prending beeminder int");
                break;
            default:
                Log.e(LOG_TAG, "Did not match any URIs for: " + uri.toString());
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

            case OLD_TASKS:
                Log.v(LOG_TAG, "Old tasks");
                break;

            case NETWORK_PENDING:
                Log.v(LOG_TAG, "Network pending");
                break;

            case NETWORK_PENDING_PAYMENT:
                Log.v(LOG_TAG, "Network pending payments");
                id = db.insert(Contract.TABLE_NETWORK_PENDING_PAYMENT, null, values);
                if (id > 0 ){
                    returnUri = Contract.buildFailedTaskUri(id);
                } else {
                    Log.e(LOG_TAG, "Failed to inert row into " + uri);
                }
                break;

            case NETWORK_PENDING_BEEMINDER_INT:
                Log.v(LOG_TAG, "Network prending beeminder int");
                break;

            default:
                Log.e(LOG_TAG, "Did not match any URIs for: " + uri.toString());
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Log.v(LOG_TAG, "Notified: " + uri.toString());
        if (returnUri != null){
            Log.v(LOG_TAG, "Returned URI: " + returnUri.toString());
        }
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

                int base_id = (int) cur.getLong(cur.getColumnIndex(Contract.KEY_ID));
                int hour_id = base_id * 100 + 60; // 60 min in an hour
                int day_id = base_id * 100 + 24; // 24 hours in a day
                int pay_id = base_id * 100 + 55; // 55 = $$

                // Clear current notifications
                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(day_id);
                mNotifyMgr.cancel(hour_id);


                // Remove notification alarms
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(context.ALARM_SERVICE);
                Intent intentHour = new Intent(context, ReminderService.class);
                PendingIntent pendingIntentHour = PendingIntent.getService(context, hour_id, intentHour, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntentHour);

                Intent intentDay = new Intent(context, ReminderService.class);
                PendingIntent pendingIntentDay = PendingIntent.getService(context, day_id, intentDay, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntentDay);

                // TODO Payment alarm
                // TODO Move to OLD_TASKS
                rowsDeleted = db.delete(Contract.TABLE_ACTIVE_TASKS, selection, selectionArgs);
                break;
            case OLD_TASKS:
                Log.v(LOG_TAG, "Old tasks");
                break;
            case NETWORK_PENDING:
                Log.v(LOG_TAG, "Network pending");
                break;
            case NETWORK_PENDING_PAYMENT:
                Log.v(LOG_TAG, "Network pending payments");
                break;
            case NETWORK_PENDING_BEEMINDER_INT:
                Log.v(LOG_TAG, "Network prending beeminder int");
                break;
            default:
                Log.e(LOG_TAG, "Did not match any URIs for: " + uri.toString());
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
            case OLD_TASKS:
                Log.v(LOG_TAG, "Old tasks");
                break;
            case NETWORK_PENDING:
                Log.v(LOG_TAG, "Network pending");
                break;
            case NETWORK_PENDING_PAYMENT:
                Log.v(LOG_TAG, "Update: Network pending payments");
                rowsUpdated = db.update(Contract.TABLE_NETWORK_PENDING_PAYMENT, values, selection, selectionArgs);
                break;
            case NETWORK_PENDING_BEEMINDER_INT:
                Log.v(LOG_TAG, "Network prending beeminder int");
                break;
            default:
                Log.e(LOG_TAG, "Did not match any URIs for: " + uri.toString());
        }

        //Return the number of rows updated
        return rowsUpdated;
    }
}
