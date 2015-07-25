package com.beeminder.gtbee.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.beeminder.gtbee.data.PaymentRequestDbHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

public class PaymentService extends IntentService {


    public PaymentService() {
        super("PaymentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        SQLiteDatabase db = new PaymentRequestDbHelper(this).getWritableDatabase();
//        Cursor cursor = db.query(
//                PaymentRequestDbHelper.TABLE_NAME, // Table name
//                null, // columns
//
//                //where
//                PaymentRequestDbHelper.COLUMN_PAYMENT_STATUS + "=\"" +
//                PaymentRequestDbHelper.NOT_CHARGED + "\"",
//
//                null, // Where args
//                null, // groupby
//                null, // having
//                null // orderby
//                );
//
//
//        // Check that there is an internet connection
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//        if (!isConnected){
//            Log.v("PaymentService", "No connection");
//            return;
//        }
//
//        //loop time!
//        while (cursor.moveToNext()){
//            //todo
//        }
//        cursor.close();
//        db.close();
//
//
//        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
//        String accessToken = settings.getString(OauthActivity.PREF_ACCESS_TOKEN, null);
//
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        String url = null;
//        try {
//            url = "https://www.beeminder.com/"
//                    + "api/v1/charges.json?amount=" + Integer.toString(payment)
//                    + "&note="+ URLEncoder.encode(title, "UTF-8")
//                    + "+via+GTBeedroid"
////                    + "&dryrun=true"
//                    + "&access_token=" + accessToken;
//        } catch (UnsupportedEncodingException e) {
//            url = "https://www.beeminder.com/"
//                    + "api/v1/charges.json?amount=" + Integer.toString(payment)
//                    + "&note=" + "unencodeable+task+name"
//                    + "+via+GTBeedroid"
////                    + "&dryrun=true"
//                    + "&access_token=" + accessToken;
//        }
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.v("PaymentService", response);
//                    }
//
//
//                }, new Response.ErrorListener(){
//                        @Override
//                        public void onErrorResponse(VolleyError vError){
//                            Log.v("PaymentService", "Error! " + vError.toString());
//                            if (vError != null
//                                    && vError.networkResponse != null
//                                    && vError.networkResponse.statusCode == 401){
//                                SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
//                                SharedPreferences.Editor editor = settings.edit();
//                                editor.putString(OauthActivity.PREF_ACCESS_TOKEN, null);
//                                editor.commit();
//                            }
//                    }
//                });
//
//
//        queue.add(stringRequest);


    }
}
