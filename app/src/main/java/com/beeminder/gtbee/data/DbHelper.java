package com.beeminder.gtbee.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nick on 22/07/15.
 */

public class DbHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GTBeeDB";

    // Table Names
    private static final String TABLE_ACTIVE_TASKS = "active_tasks";
    private static final String TABLE_OLD_TASKS = "old_tasks";
    private static final String TABLE_NETWORK_PENDING_PAYMENT = "network_pending_payment";
    private static final String TABLE_NETWORK_PENDING_BEEMINDER_INT = "network_pending_beeminder_int";

    // Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DUE_DATE = "due_date";
    private static final String KEY_ADDED_DATE = "added_date";
    private static final String KEY_PENALTY = "penalty";
    private static final String KEY_DESCRIPTION = "description";

    // ACTIVE_TASKS column names


    // OLD_TASKS column names
    private static final String KEY_DONE_DATE = "done_date";

    // NETWORK_PENDING_PAYMENT column names
    private static final String KEY_PAYMENT_STATUS = "payment_status";

    // NETWORK_PENDING_BEEMINDER_INT column names
    private static final String KEY_SENT_STATUS = "sent_status";


    // Table Create Statements

    // ACTIVE_TASKS create statement
    private static final String CREATE_ACTIVE_TASKS = "CREATE TABLE " + TABLE_ACTIVE_TASKS +
            " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " TEXT NOT NULL, " +
            KEY_DUE_DATE + " INTEGER NOT NULL, " +
            KEY_ADDED_DATE + " INTEGER NOT NULL, " +
            KEY_PENALTY + " INTEGER NOT NULL, " +
            KEY_DESCRIPTION + " TEXT NOT NULL" +
            ");";

    // OLD_TASKS create statement
    private static final String CREATE_OLD_TASKS = "CREATE TABLE " + TABLE_OLD_TASKS +
            " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " TEXT NOT NULL, " +
            KEY_DUE_DATE + " INTEGER NOT NULL, " +
            KEY_ADDED_DATE + " INTEGER NOT NULL, " +
            KEY_PENALTY + " INTEGER NOT NULL, " +
            KEY_DESCRIPTION + " TEXT NOT NULL, " +
            KEY_DONE_DATE + " INTEGER NOT NULL"+
            ");";

    // NETWORK_PENDING_PAYMENT  create statement
    private static final String CREATE_NETWORK_PENDING_PAYMENT =  "CREATE TABLE " + TABLE_NETWORK_PENDING_PAYMENT +
            " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " TEXT NOT NULL, " +
            KEY_PENALTY + " INTEGER NOT NULL, " +
            KEY_PAYMENT_STATUS + " TEXT NOT NULL, " +
            KEY_DUE_DATE + " INTEGER NOT NULL " +
            ");";

    // NETWORK_PENDING_BEEMINDER_INT create statement
    private static final String CREATE_NETWORK_PENDING_BEEMINDER_INT = "CREATE TABLE " + TABLE_NETWORK_PENDING_BEEMINDER_INT +
            " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " TEXT NOT NULL, " +
            KEY_SENT_STATUS + " TEXT NOT NULL" +
            ");";





    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create required tables
        db.execSQL(CREATE_ACTIVE_TASKS);
        db.execSQL(CREATE_OLD_TASKS);
        db.execSQL(CREATE_NETWORK_PENDING_PAYMENT);
        db.execSQL(CREATE_NETWORK_PENDING_BEEMINDER_INT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OLD_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NETWORK_PENDING_PAYMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NETWORK_PENDING_BEEMINDER_INT);

        // create new tables
        onCreate(db);

    }
}
