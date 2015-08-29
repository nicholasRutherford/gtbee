package com.beeminder.gtbee;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beeminder.gtbee.auth.OauthActivity;
import com.beeminder.gtbee.data.Contract;


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
        long time_mili = mCallback.getDate();
        long oldID = ((NewTask) getActivity()).mOldTaskID;
        int retryCount = ((NewTask) getActivity()).mRetryNumber;

        if (oldID != -1l){
            Cursor cur = getActivity().getContentResolver().query(Contract.ACTIVE_TASKS_URI, null,
                    Contract.KEY_ID + "=" + oldID, null, null);
            String title = cur.getString(cur.getColumnIndexOrThrow(Contract.KEY_TITLE));
            EditText editText = (EditText) view.findViewById(R.id.new_task_title);
            editText.setText(title);

        }
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
