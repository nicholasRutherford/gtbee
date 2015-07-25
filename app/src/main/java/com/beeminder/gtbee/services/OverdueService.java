package com.beeminder.gtbee.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;


import com.beeminder.gtbee.data.Contract;

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
        int payed = 0;
        Long dueDate = Calendar.getInstance().getTimeInMillis();


        ContentValues values = new ContentValues();
        values.put(Contract.KEY_TITLE, title);
        values.put(Contract.KEY_PENALTY, paymentAmount);
        values.put(Contract.KEY_PAYED, payed);
        values.put(Contract.KEY_DUE_DATE, dueDate);

        getContentResolver().insert(Contract.FAILED_TASKS_URI,values);

        Intent paymentIntent = new Intent(this, PaymentService.class);
        startService(paymentIntent);
    }
}
