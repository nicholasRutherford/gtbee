package com.beeminder.gtbee.services;

import android.app.IntentService;
import android.content.Intent;

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
