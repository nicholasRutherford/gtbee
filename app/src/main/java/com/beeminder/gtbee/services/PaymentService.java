package com.beeminder.gtbee.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beeminder.gtbee.Utility;
import com.beeminder.gtbee.auth.OauthActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

public class PaymentService extends IntentService {

    public static final String TASK_TITLE = "com.beeminder.gtbee.task_title";
    public static final String TASK_ID = "com.beeminder.gtbee.task_id";
    public static final String ATTEMPT_NUMBER = "com.beeminder.gtbee.attempt_number";
    public static final String PAYMENT_AMOUNT = "com.beeminder.gtbee.payment_amount";

    public static final int MAX_ATTEMPS = 50;
    public static final int BASE_TIME = 30*1000; //30 sec

    private Intent mIntent;


    public PaymentService() {
        super("PaymentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mIntent = intent;
        String title = intent.getStringExtra(TASK_TITLE);
        int attempt = intent.getIntExtra(ATTEMPT_NUMBER, 0);
        int payment = intent.getIntExtra(PAYMENT_AMOUNT, 0);

        // Check if we've reached maximum attemps
        if(attempt > MAX_ATTEMPS){
            Log.v("PaymentService", "Hit Maximum attempts("+ Integer.toString(MAX_ATTEMPS)
                    +  ") on: " + title);
            return;
        }

        // Check that there is an internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected){
            Log.v("PaymentService", "No connection, reschudeling payment(attempt "
                    + Integer.toString(attempt) + ")");
            requeueIntent();
            return;
        }


        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String accessToken = settings.getString(OauthActivity.PREF_ACCESS_TOKEN, null);


        RequestQueue queue = Volley.newRequestQueue(this);

        String url = null;
        try {
            url = "https://www.beeminder.com/"
                    + "api/v1/charges.json?amount=" + Integer.toString(payment)
                    + "&note="+ URLEncoder.encode(title, "UTF-8")
//                    + "&dryrun=true"
                    + "&access_token=" + accessToken;
        } catch (UnsupportedEncodingException e) {
            url = "https://www.beeminder.com/"
                    + "api/v1/charges.json?amount=" + Integer.toString(payment)
                    + "&note=" + "unencodeable+task+name"
//                    + "&dryrun=true"
                    + "&access_token=" + accessToken;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("PaymentService", response);
                    }


                }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError vError){
                            Log.v("PaymentService", "Error! " + vError.toString());
                            if (vError != null
                                    && vError.networkResponse != null
                                    && vError.networkResponse.statusCode == 401){
                                SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(OauthActivity.PREF_ACCESS_TOKEN, null);
                                editor.commit();
                            }
                            requeueIntent(); //TODO
                    }
                });


        queue.add(stringRequest);


    }

    private void requeueIntent(){
        String title = mIntent.getStringExtra(TASK_TITLE);
        int base_id = mIntent.getIntExtra(TASK_ID, 0);
        int attemptNumber = mIntent.getIntExtra(ATTEMPT_NUMBER, 0) + 1;
        int paymentAmount = mIntent.getIntExtra(PAYMENT_AMOUNT, 0);

        int pay_id = base_id*100 + 55;

        Long currentTime = Calendar.getInstance().getTimeInMillis();
        Long dueTime = currentTime + (BASE_TIME * Math.round(Math.pow(2, attemptNumber)));


        Intent intentPayment = new Intent(this, PaymentService.class);
        intentPayment.putExtra(TASK_TITLE, title);
        intentPayment.putExtra(TASK_ID, base_id);
        intentPayment.putExtra(ATTEMPT_NUMBER, attemptNumber);
        intentPayment.putExtra(PAYMENT_AMOUNT, paymentAmount);

        PendingIntent pendingIntentPayment = PendingIntent.getService(this, pay_id, intentPayment, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, dueTime, pendingIntentPayment);
        Log.v("PaymentService", "Payment reschedueled for: " + new Utility().niceDateTime(dueTime));

    }
}
