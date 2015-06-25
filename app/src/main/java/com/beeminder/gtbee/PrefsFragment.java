package com.beeminder.gtbee;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.beeminder.gtbee.R;

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}