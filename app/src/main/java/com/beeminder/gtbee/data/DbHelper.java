package com.beeminder.gtbee.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nick on 22/07/15.
 */

public class DbHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = this.getClass().getSimpleName();


    // Table Create Statements

    // ACTIVE_TASKS create statement
    private static final String CREATE_ACTIVE_TASKS = "CREATE TABLE " + Contract.TABLE_ACTIVE_TASKS +
            " (" +
            Contract.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Contract.KEY_TITLE + " TEXT NOT NULL, " +
            Contract.KEY_DUE_DATE + " INTEGER NOT NULL, " +
            Contract.KEY_ADDED_DATE + " INTEGER NOT NULL, " +
            Contract.KEY_PENALTY + " INTEGER NOT NULL, " +
            Contract.KEY_DESCRIPTION + " TEXT NOT NULL," +
            Contract.KEY_RETRY_NUMBER + " INTEGER NOT NULL " +
            ");";

    // OLD_TASKS create statement
    private static final String CREATE_COMPLETED_TASKS = "CREATE TABLE " + Contract.TABLE_COMPLETED_TASKS +
            " (" +
            Contract.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Contract.KEY_TITLE + " TEXT NOT NULL, " +
            Contract.KEY_DUE_DATE + " INTEGER NOT NULL, " +
            Contract.KEY_ADDED_DATE + " INTEGER NOT NULL, " +
            Contract.KEY_PENALTY + " INTEGER NOT NULL, " +
            Contract.KEY_DESCRIPTION + " TEXT NOT NULL, " +
            Contract.KEY_DONE_DATE + " INTEGER NOT NULL"+
            ");";

    // NETWORK_PENDING_PAYMENT  create statement
    private static final String CREATE_FAILED_TASKS =  "CREATE TABLE " + Contract.TABLE_NETWORK_PENDING_PAYMENT +
            " (" +
            Contract.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Contract.KEY_TITLE + " TEXT NOT NULL, " +
            Contract.KEY_PENALTY + " INTEGER NOT NULL, " +
            Contract.KEY_PAYED + " INTEGER NOT NULL, " +
            Contract.KEY_DUE_DATE + " INTEGER NOT NULL " +
            ");";

    // NETWORK_PENDING_BEEMINDER_INT create statement
    private static final String CREATE_NETWORK_PENDING_BEEMINDER_INT = "CREATE TABLE " + Contract.TABLE_NETWORK_PENDING_BEEMINDER_INT +
            " (" +
            Contract.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Contract.KEY_TITLE + " TEXT NOT NULL, " +
            Contract.KEY_SENT_STATUS + " TEXT NOT NULL" +
            ");";





    public DbHelper(Context context) {
        super(context, Contract.DATABASE_NAME, null, Contract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create required tables
        db.execSQL(CREATE_ACTIVE_TASKS);
        db.execSQL(CREATE_COMPLETED_TASKS);
        db.execSQL(CREATE_FAILED_TASKS);
        db.execSQL(CREATE_NETWORK_PENDING_BEEMINDER_INT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_ACTIVE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_COMPLETED_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_NETWORK_PENDING_PAYMENT);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_NETWORK_PENDING_BEEMINDER_INT);

        // create new tables
        onCreate(db);

    }
}
