package com.beeminder.gtbee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.beeminder.gtbee.auth.BeeminderAuthInfo;
import com.beeminder.gtbee.auth.OauthActivity;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setElevation(0f);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String accessToken = settings.getString(OauthActivity.PREF_ACCESS_TOKEN, null);
        Log.v("main", "Checking access tocken " + accessToken);
        if (accessToken == null){
            Intent intent = new Intent(this, BeeminderAuthInfo.class);
            startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new PrefsFragment()).commit();
        }
//        if (id == R.id.action_add_freebie){
//            SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
//            int haveFree = settings.getInt(OauthActivity.PREF_FREEBIES,0);
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putInt(OauthActivity.PREF_FREEBIES, haveFree+1);
//            editor.commit();
//        }


        return super.onOptionsItemSelected(item);
    }

    public void add_task(View view){
        Intent intent = new Intent(this, NewTask.class);
        intent.putExtra(NewTask.TASK_NAME, "");
        intent.putExtra(NewTask.RETRY_NUMBER, 0);
        startActivity(intent);
    }
}
