package com.beeminder.gtbee.services;

import android.app.IntentService;
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
import com.beeminder.gtbee.auth.OauthActivity;
import com.beeminder.gtbee.data.Contract;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PaymentService extends IntentService {
    private final String LOG_TAG = this.getClass().getSimpleName();

    public PaymentService() {
        super("PaymentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        // Check that there is an internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected){
            Log.v("PaymentService", "No connection");
            return;
        }
        Cursor cursor = getContentResolver().query(Contract.FAILED_TASKS_URI, null,
                Contract.KEY_PAYED + "=" + 0, null, null);

        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String accessToken = settings.getString(OauthActivity.PREF_ACCESS_TOKEN, null);

        RequestQueue queue = Volley.newRequestQueue(this);

        //loop time!
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(Contract.KEY_TITLE));
            int payment = cursor.getInt(cursor.getColumnIndex(Contract.KEY_PENALTY));
            long id = cursor.getLong(cursor.getColumnIndex(Contract.KEY_ID));
            StringRequest request = createStringRequest(accessToken,payment, title, id);
            queue.add(request);
        }

    }

    private StringRequest createStringRequest(String accessToken, int paymentAmount,
                                              String title, long id){

        String url = null;
        try {
            url = "https://www.beeminder.com/"
                    + "api/v1/charges.json?amount=" + Integer.toString(paymentAmount)
                    + "&note="+ URLEncoder.encode(title, "UTF-8")
                    + "+via+GTBeedroid"
                    + "&dryrun=true"
                    + "&access_token=" + accessToken;
        } catch (UnsupportedEncodingException e) {
            url = "https://www.beeminder.com/"
                    + "api/v1/charges.json?amount=" + Integer.toString(paymentAmount)
                    + "&note=" + "unencodeable+task+name"
                    + "+via+GTBeedroid"
                    + "&dryrun=true"
                    + "&access_token=" + accessToken;
        }

        return  new StringRequest(Request.Method.POST, url,
                new PaymentListener(id),  new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError vError){
                Log.v(LOG_TAG, "Error! " + vError.toString());
                if (vError != null
                        && vError.networkResponse != null
                        && vError.networkResponse.statusCode == 401){
                    SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(OauthActivity.PREF_ACCESS_TOKEN, null);
                    editor.apply();
                }
            }
        });
    }


    class PaymentListener implements Response.Listener<String>{
        private long mTaskId;

        public PaymentListener(long id) {
            super();
            mTaskId = id;
        }

        @Override
        public void onResponse(String response) {
            int payed = 1;
            ContentValues values = new ContentValues();
            values.put(Contract.KEY_PAYED, payed);

            getContentResolver().update(Contract.FAILED_TASKS_URI, values,
                    Contract.KEY_ID + "=" + mTaskId, null);
        }
    }

}
