package com.beeminder.gtbee;

import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beeminder.gtbee.data.Contract;

import java.util.Calendar;


/**
 * A placeholder fragment containing a simple view.
 */
public class TaskDetailFragment extends Fragment {
    private static Long sec_mili = 1000l;
    private static Long min_mili = sec_mili*60;
    private static Long hour_mili = min_mili*60;
    private static Long day_mili = hour_mili*24;


    public TaskDetailFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);
        Long taskID = ((TaskDetail) getActivity()).mTaskId;

        Cursor cur = getActivity().getContentResolver().query(Contract.ACTIVE_TASKS_URI,
                null, Contract.KEY_ID + "=" + taskID, null, null);

        cur.moveToFirst();
        Long dateAdded = cur.getLong(cur.getColumnIndex(Contract.KEY_ADDED_DATE));
        Long dateDue = cur.getLong(cur.getColumnIndex(Contract.KEY_DUE_DATE));
        int penalty = cur.getInt(cur.getColumnIndex(Contract.KEY_PENALTY));

        TextView dateAddedView = (TextView) view.findViewById(R.id.detail_task_date_added);
        dateAddedView.setText(new Utility().niceDateTime(dateAdded));

        TextView dateDueView = (TextView) view.findViewById(R.id.detail_task_date_due);
        dateDueView.setText(new Utility().niceDateTime(dateDue));

        TextView penaltyView = (TextView) view.findViewById(R.id.detail_task_penalty);
        penaltyView.setText(new Utility().formatPenalty(penalty));

        final TextView daysTimes = (TextView) view.findViewById(R.id.detail_task_timer_day);
        final TextView hoursTimer = (TextView) view.findViewById(R.id.detail_task_timer_hour);
        final TextView minTimer = (TextView) view.findViewById(R.id.detail_task_timer_min);
        final TextView secTimer = (TextView) view.findViewById(R.id.detail_task_timer_sec);

        Long currentTime = Calendar.getInstance().getTimeInMillis();

        new CountDownTimer(dateDue - currentTime, 1000){

            public void onTick(long millToFiish){
                double time = (double) millToFiish;

                double days = Math.floor(time/day_mili);
                time = time - (days* day_mili);

                double hours = Math.floor(time/hour_mili);
                time = time - (hours* hour_mili);

                double mins = Math.floor(time/min_mili);
                time = time - (mins* min_mili);

                double secs = Math.floor(time/sec_mili);
                time = time - (secs* sec_mili);

                daysTimes.setText(String.format("%.0f", days));
                hoursTimer.setText(String.format("%.0f", hours));
                minTimer.setText(String.format("%.0f", mins));
                secTimer.setText(String.format("%.0f", secs));

            }

            public void onFinish(){
                //TODO change fragment to past due one
                secTimer.setText("0");

            }
        }.start();

        return view;
    }

}
