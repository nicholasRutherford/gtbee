package com.beeminder.gtbee.integrations;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beeminder.gtbee.R;
import com.beeminder.gtbee.auth.OauthActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BeeminederIntActivity extends ActionBarActivity {
    public String[] mGoals;
    public static final String BEEMINDER_GOAL = "com.beeminder.gtbee.beeminder_goal";
    public boolean goalSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String beeminderGoal = settings.getString(BEEMINDER_GOAL, null);
        Log.v("BeeminderIngegration", "Checking goal: " + beeminderGoal);
        goalSet = !(beeminderGoal == null);

        setContentView(R.layout.activity_beemineder_int);
        getSupportActionBar().setTitle(getResources().getString(R.string.beeminder_int_title));
        if (goalSet){
            Spinner spinner = (Spinner) findViewById(R.id.beemind_int_spinner);
            spinner.setVisibility(View.INVISIBLE);
        } else {
            setupSpinner();
        }

    }

    public void buttonClick(View view){
        if (goalSet) {
            SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(BEEMINDER_GOAL, null);
            editor.commit();
            goalSet = false;
            finish();

        }else {
            Spinner spinner = (Spinner) findViewById(R.id.beemind_int_spinner);
            TextView textView = (TextView) spinner.getSelectedView().findViewById(R.id.spinner_item_text);
            String goal = textView.getText().toString();


            Log.v("BeemindIntActivity", "Goal: " + goal);
            SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(BEEMINDER_GOAL, goal);
            editor.commit();
            goalSet = true;
            finish();
        }

    }

    public void setupSpinner(){

        // Check that there is an internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected){
            Log.v("PaymentService", "No connection, can't connect to beeminder");
            return;
        }


        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String accessToken = settings.getString(OauthActivity.PREF_ACCESS_TOKEN, null);


        RequestQueue queue = Volley.newRequestQueue(this);

        String url = null;

        url = "https://www.beeminder.com/"
                + "api/v1/users/me.json?"
                + "&access_token=" + accessToken;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("BeeminderIntegration", response);
                        JSONObject request = null;

                        //Parse main object
                        try {
                            request = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONArray goalArray = null;
                        // Parse goals array
                        try {
                            goalArray = request.getJSONArray("goals");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ArrayList<String> arrayList = new ArrayList<String>();
                       for (int i=0; i < goalArray.length(); i++){
                           String goalName = null;
                           try {
                               goalName = goalArray.getString(i);
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                           arrayList.add(goalName);

                       }

                        mGoals = new String[arrayList.size()];
                        mGoals = arrayList.toArray(mGoals);

                        BeeminederIntActivityFragment frag =
                                (BeeminederIntActivityFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.beemind_int_frag);

                        Spinner spinner = (Spinner) findViewById(R.id.beemind_int_spinner);

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.spinner_item, R.id.spinner_item_text, mGoals);

                        //spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item,R.layout.spinner_item);
                        spinner.setAdapter(spinnerArrayAdapter);


                    }


                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError vError){
                Log.v("BeeminderIntegration", "Error! " + vError.toString());
                if (vError != null
                        && vError.networkResponse != null
                        && vError.networkResponse.statusCode == 401){
                    SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(OauthActivity.PREF_ACCESS_TOKEN, null);
                    editor.commit();
                }
            }
        });


        queue.add(stringRequest);
    }
}
