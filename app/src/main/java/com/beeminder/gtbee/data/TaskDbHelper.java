package com.beeminder.gtbee.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by nick on 11/06/15.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    // and fill in the onUpgrade method
    private static final int DATABASE_VERSION = 6;

    static final String DATABASE_NAME = "tasks.db";

    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_ADDED_DATE = "added_date";
    public static final String COLUMN_PENALTY = "penalty";
    public static final String COLUMN_RETRY_COUNT ="retry_count";

    public TaskDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_DUE_DATE = 2;
    public static final int COL_ADDED_DATE = 3;
    public static final int COL_PENALTY = 4;
    public static final int COL_RETRY = 5;


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +

                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_DUE_DATE + " INTEGER NOT NULL, " +
                COLUMN_ADDED_DATE + " INTEGER NOT NULL, " +
                COLUMN_PENALTY + " INTEGER NOT NULL, " +
                COLUMN_RETRY_COUNT + " INTEGER NOT NULL " +
                ");";

        db.execSQL(SQL_CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Operation to run when we update the schema of the db
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
