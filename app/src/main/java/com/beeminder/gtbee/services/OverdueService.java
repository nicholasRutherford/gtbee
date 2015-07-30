package com.beeminder.gtbee.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;


import com.beeminder.gtbee.data.Contract;

import java.util.Calendar;


public class OverdueService extends IntentService {
    public static final String TASK_ID = "com.beeminder.gtbee.task_id";

    public OverdueService(){ super("OverdueService");}

    @Override
    protected void onHandleIntent(Intent intent) {
        int id = intent.getIntExtra(TASK_ID, -1);

        Cursor cur = getContentResolver().query(Contract.ACTIVE_TASKS_URI, null,
                Contract.KEY_ID + "=" +id , null, null);


        if (cur.getCount() == 1) {
            cur.moveToFirst();

            String title = cur.getString(cur.getColumnIndex(Contract.KEY_TITLE));
            int paymentAmount = cur.getInt(cur.getColumnIndex(Contract.KEY_PENALTY));
            Long dueDate = cur.getLong(cur.getColumnIndex(Contract.KEY_DUE_DATE));
            int payed = 0; // Set payment status to not payed

            ContentValues values = new ContentValues();
            values.put(Contract.KEY_TITLE, title);
            values.put(Contract.KEY_PENALTY, paymentAmount);
            values.put(Contract.KEY_PAYED, payed);
            values.put(Contract.KEY_DUE_DATE, dueDate);

            getContentResolver().insert(Contract.FAILED_TASKS_URI, values);

            Intent paymentIntent = new Intent(this, PaymentService.class);
            startService(paymentIntent);
        }
    }
}
