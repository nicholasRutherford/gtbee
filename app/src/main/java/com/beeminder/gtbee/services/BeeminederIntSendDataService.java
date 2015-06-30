package com.beeminder.gtbee.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beeminder.gtbee.Utility;
import com.beeminder.gtbee.auth.OauthActivity;
import com.beeminder.gtbee.integrations.BeeminederIntActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;


public class BeeminederIntSendDataService extends IntentService {

    public static final String TASK_TITLE = "com.beeminder.gtbee.task_title";
    public static final String TASK_ID = "com.beeminder.gtbee.task_id";
    public static final String ATTEMPT_NUMBER = "com.beeminder.gtbee.attempt_number";

    public static final int MAX_ATTEMPS = 50;
    public static final int BASE_TIME = 30*1000; //30 sec

    private Intent mIntent;

    public BeeminederIntSendDataService() {
        super("BeemindIntService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mIntent = intent;
        String title = intent.getStringExtra(TASK_TITLE);
        int attempt = intent.getIntExtra(ATTEMPT_NUMBER, 0);

        // Check if we've reached maximum attemps
        if(attempt > MAX_ATTEMPS){
            Log.v("BeemindIntService", "Hit Maximum attempts(" + Integer.toString(MAX_ATTEMPS)
                    + ") on: " + title);
            return;
        }

        // Check that there is an internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected){
            Log.v("BeemindIntService", "No connection, reschudeling data add(attempt "
                    + Integer.toString(attempt) + ")");
            requeueIntent();
            return;
        }


        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String accessToken = settings.getString(OauthActivity.PREF_ACCESS_TOKEN, null);
        String beeminderGoal = settings.getString(BeeminederIntActivity.BEEMINDER_GOAL, null);


        RequestQueue queue = Volley.newRequestQueue(this);

        String url = null;
        try {
            url = "https://www.beeminder.com"
                    + "/api/v1/users/me/goals/" + beeminderGoal
                    + "/datapoints.json"
                    + "?value=1"
                    + "&comment=" + URLEncoder.encode(title,"UTF-8")
                    + "&access_token=" + accessToken;
        } catch (UnsupportedEncodingException e) {
            url = "https://www.beeminder.com"
                    + "/api/v1/users/me/goals/" +beeminderGoal
                    + "/datapoints.json?value=1"
                    +"&comment=" + "unencodeable+task+name"
                    + "&access_token=" + accessToken;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("BeemindIntService", response);
                    }


                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError vError){
                Log.v("BeemindIntService", "Error! " + vError.toString());
                if (vError != null
                        && vError.networkResponse != null
                        && vError.networkResponse.statusCode == 500){
                    // The beeminder goal no longer exists
                    Log.v("BeemindIntService", "Goal no longer exists, remove beeminder link");
                    SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(BeeminederIntActivity.BEEMINDER_GOAL, null);
                    editor.commit();
                }
                if (vError != null
                        && vError.networkResponse != null
                        && vError.networkResponse.statusCode == 401){
                    // User access token didn't work
                    Log.v("BeemindIntService", "Access token no longer works, get user to authenticate again");
                    SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(OauthActivity.PREF_ACCESS_TOKEN, null);
                    editor.commit();
                    requeueIntent();
                }

            }
        });


        queue.add(stringRequest);


    }

    private void requeueIntent(){
        String title = mIntent.getStringExtra(TASK_TITLE);
        int base_id = mIntent.getIntExtra(TASK_ID, 0);
        int attemptNumber = mIntent.getIntExtra(ATTEMPT_NUMBER, 0) + 1;

        int send_id = base_id*100 + 88;

        Long currentTime = Calendar.getInstance().getTimeInMillis();
        Long sendTime = currentTime + (BASE_TIME * Math.round(Math.pow(2, attemptNumber)));


        Intent intentSendData = new Intent(this, BeeminederIntSendDataService.class);
        intentSendData.putExtra(TASK_TITLE, title);
        intentSendData.putExtra(TASK_ID, base_id);
        intentSendData.putExtra(ATTEMPT_NUMBER, attemptNumber);

        PendingIntent pendingIntentSendData = PendingIntent.getService(this, send_id,
                intentSendData, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, sendTime, pendingIntentSendData);
        Log.v("BeemindIntService", "Data add rescheduled for: " + new Utility().niceDateTime(sendTime));

    }
}


