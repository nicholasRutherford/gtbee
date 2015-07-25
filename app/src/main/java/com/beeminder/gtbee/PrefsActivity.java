package com.beeminder.gtbee;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class PrefsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        getSupportActionBar().setTitle("Settings");
    }
}
