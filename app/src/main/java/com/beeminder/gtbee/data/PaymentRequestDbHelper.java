package com.beeminder.gtbee.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nick on 15/07/15.
 */
public class PaymentRequestDbHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String NOT_CHARGED = "pending";
    public static final String CHARGES = "charged";

    // If you change the database schema, you must increment the database version.
// and fill in the onUpgrade method
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "requests.db";

    public static final String TABLE_NAME = "payment_requests";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PAYMENT_AMOUNT = "payment_amount";
    public static final String COLUMN_PAYMENT_STATUS = "payment_status";
    public static final String COLUMN_DUE_DATE = "due_date";

    public PaymentRequestDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_PAYMENT_AMOUNT = 2;
    public static final int COL_PAYMENT_STATUS = 3;
    public static final int COL_DUE_DATE = 4;



    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +

                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_PAYMENT_AMOUNT + " INTEGER NOT NULL, " +
                COLUMN_PAYMENT_STATUS + " TEXT NOT NULL, " +
                COLUMN_DUE_DATE + " INTEGER NOT NULL " +
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
