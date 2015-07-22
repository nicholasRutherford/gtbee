package com.beeminder.gtbee.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, "active", ACTIVE_TASKS);
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

        switch (sUriMatcher.match(uri)) {
            case ACTIVE_TASKS:
                Log.v(LOG_TAG, "Active_Task");
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

        // return the data as a cursor object
        return null;
    }

    @Override
    public String getType(Uri uri) {

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

        // Return a content URI for the newly-inserted row
        return null;
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
