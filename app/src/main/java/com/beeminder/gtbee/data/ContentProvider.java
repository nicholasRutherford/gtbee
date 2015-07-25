package com.beeminder.gtbee.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "old", OLD_TASKS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "network_pending", NETWORK_PENDING);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "network_pending/payment", NETWORK_PENDING_PAYMENT);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "network_pending/beeminder_int", NETWORK_PENDING_BEEMINDER_INT);
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
                cursor = db.query(Contract.TABLE_ACTIVE_TASKS, projection,selection, selectionArgs, null, null, sortOrder);
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
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // return the data as a cursor object
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
        switch (sUriMatcher.match(uri)) {
            case ACTIVE_TASKS:
                long id = db.insert(Contract.TABLE_ACTIVE_TASKS, null, values);
                if (id > 0){
                    returnUri = Contract.buildTaskUri(id);
                } else {
                    Log.e(LOG_TAG, "Failed to insert row into " + uri);
                }
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

        getContext().getContentResolver().notifyChange(uri, null);
        Log.v(LOG_TAG, "Notified: " + uri.toString());
        // Return a content URI for the newly-inserted row
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Return the number of rows deleted
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Return the number of rows updated
        return 0;
    }
}
