package com.beeminder.gtbee.integrations;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beeminder.gtbee.R;
import com.beeminder.gtbee.auth.OauthActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class BeeminederIntActivityFragment extends Fragment {

    public BeeminederIntActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_beemineder_int, container, false);

        Button button = (Button) view.findViewById(R.id.beemind_int_button);

        BeeminederIntActivity activity = (BeeminederIntActivity) getActivity();
        if (activity.goalSet){
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.beemind_int_spinner_layout);
            linearLayout.setVisibility(View.INVISIBLE);
            TextView textView = (TextView) view.findViewById(R.id.beemind_int_current_goal);
            SharedPreferences settings = getActivity().getSharedPreferences(OauthActivity.PREF_NAME, getActivity().MODE_PRIVATE);
            String beeminderGoal = settings.getString(BeeminederIntActivity.BEEMINDER_GOAL, null);
            textView.setText(beeminderGoal);
            button.setText("Remove Link");
        } else{
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.beemind_int_current_goal_layout);
            linearLayout.setVisibility(View.INVISIBLE);
            button.setText("Link To Goal");
        }
        return  view;
    }
}
