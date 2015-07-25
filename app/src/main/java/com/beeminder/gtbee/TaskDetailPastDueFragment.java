package com.beeminder.gtbee;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**

 */
public class TaskDetailPastDueFragment extends Fragment {

    public TaskDetailPastDueFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_detail_past_due, container, false);
    }
}
