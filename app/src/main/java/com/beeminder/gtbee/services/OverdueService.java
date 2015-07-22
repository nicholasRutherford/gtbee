package com.beeminder.gtbee.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.beeminder.gtbee.data.PaymentRequestDbHelper;
import com.beeminder.gtbee.data.TaskDbHelper;

import java.util.Calendar;


public class OverdueService extends IntentService {
    public static final String TASK_TITLE = "com.beeminder.gtbee.task_title";
    public static final String TASK_ID = "com.beeminder.gtbee.task_id";
    public static final String ATTEMPT_NUMBER = "com.beeminder.gtbee.attempt_number";
    public static final String PAYMENT_AMOUNT = "com.beeminder.gtbee.payment_amount";

    public OverdueService(){ super("OverdueService");}

    @Override
    protected void onHandleIntent(Intent intent) {

        String title =  intent.getStringExtra(TASK_TITLE);
        int paymentAmount =  intent.getIntExtra(PAYMENT_AMOUNT, 0);
        String paymentStatus = PaymentRequestDbHelper.NOT_CHARGED;
        Long dueDate = Calendar.getInstance().getTimeInMillis();

        SQLiteDatabase db = new PaymentRequestDbHelper(this).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PaymentRequestDbHelper.COLUMN_TITLE, title);
        values.put(PaymentRequestDbHelper.COLUMN_PAYMENT_AMOUNT, paymentAmount);
        values.put(PaymentRequestDbHelper.COLUMN_PAYMENT_STATUS, paymentStatus);
        values.put(PaymentRequestDbHelper.COLUMN_DUE_DATE, dueDate);

        db.insert(TaskDbHelper.TABLE_NAME, null, values);
        db.close();

        Intent paymentIntent = new Intent(this, PaymentService.class);
        startService(paymentIntent);
    }
}
