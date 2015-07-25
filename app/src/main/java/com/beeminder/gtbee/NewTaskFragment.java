package com.beeminder.gtbee;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beeminder.gtbee.auth.OauthActivity;


/**
 * A placeholder fragment containing a simple view.
 */
public class NewTaskFragment extends Fragment{
    DateListener mCallback;

    public interface DateListener {
        public Long getDate();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (DateListener) activity;
    }

    public NewTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);
        Long time_mili = mCallback.getDate();
        String oldName = ((NewTask) getActivity()).oldTaskName;
        int retryCount = ((NewTask) getActivity()).retryNumber;

        EditText editText = (EditText) view.findViewById(R.id.new_task_title);
        editText.setText(oldName);

        TextView textView = (TextView) view.findViewById(R.id.new_task_due_date);
        textView.setText(new Utility().niceDateTime(time_mili));


        SharedPreferences settings = getActivity().getSharedPreferences(OauthActivity.PREF_NAME, getActivity().MODE_PRIVATE);
        int freebies = settings.getInt(OauthActivity.PREF_FREEBIES, 0);

        if (freebies <1 || retryCount > 0){
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.new_task_freebie_layout);
            linearLayout.setVisibility(View.INVISIBLE);
        } else {
            TextView freeBieVeiew = (TextView) view.findViewById(R.id.new_task_freebie_text);
            freeBieVeiew.setText("Freebie ("+ Integer.toString(freebies) + " remaining):");
        }
        return view;
    }

}
