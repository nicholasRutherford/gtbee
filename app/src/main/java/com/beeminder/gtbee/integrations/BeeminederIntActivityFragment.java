package com.beeminder.gtbee.integrations;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beeminder.gtbee.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BeeminederIntActivityFragment extends Fragment {

    public BeeminederIntActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_beemineder_int, container, false);
    }
}
