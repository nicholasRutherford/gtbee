package com.beeminder.gtbee.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.beeminder.gtbee.data.Contract;
import com.beeminder.gtbee.integrations.BeeminederIntActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;


public class BeeminederIntSendDataService extends IntentService {
    private String LOG_TAG = this.getClass().getSimpleName();


    private Intent mIntent;

    public BeeminederIntSendDataService() {
        super("BeemindIntService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        // Check that there is an internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected){
            Log.v("BeemindIntService", "No connection");
            return;
        }


        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String accessToken = settings.getString(OauthActivity.PREF_ACCESS_TOKEN, null);
        String beeminderGoal = settings.getString(BeeminederIntActivity.BEEMINDER_GOAL, null);

        Cursor cursor = getContentResolver().query(Contract.NETWORK_PENDING_BEEMINDER_INT_URI, null,
                Contract.KEY_SENT_STATUS + "=" + 0, null, null);

        RequestQueue queue = Volley.newRequestQueue(this);

        //loop time!
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndexOrThrow(Contract.KEY_TITLE));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(Contract.KEY_ID));

            StringRequest request = createStringRequest(accessToken,beeminderGoal, title, id);
            queue.add(request);
        }

    }

    private StringRequest createStringRequest(String accessToken,  String beeminderGoal,
                                              String title, long id){

        String url;
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
                new BeeminderListener(id), new Response.ErrorListener(){
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
                }
            }
        });

        return stringRequest;
    }



    class BeeminderListener implements Response.Listener<String>{
        private long mTaskId;

        public BeeminderListener(long id) {
            super();
            mTaskId = id;
        }

        @Override
        public void onResponse(String response) {

            getContentResolver().delete(Contract.NETWORK_PENDING_BEEMINDER_INT_URI,
                    Contract.KEY_ID + "=" + mTaskId, null);
        }
    }
}


