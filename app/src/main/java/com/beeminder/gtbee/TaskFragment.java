package com.beeminder.gtbee;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.beeminder.gtbee.data.TaskContract;
import com.beeminder.gtbee.data.TaskDbHelper;


/**
 * A placeholder fragment containing a simple view.
 */
public class TaskFragment extends Fragment {
    public TaskAdapter mTaskAdapter;
    private ListView mListView;
    public final static String EXTRA_MESSAGE = "com.beeminder.gtbee.TITLE_MESSAGE";
    private Cursor cursor;

    private static final int TASK_LOADER = 0;

    public TaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cursor = new TaskDbHelper(this.getActivity()).getReadableDatabase().query(TaskDbHelper.TABLE_NAME,
                null, null, null, null, null, TaskDbHelper.COLUMN_DUE_DATE + " ASC;");
        mTaskAdapter = new TaskAdapter(getActivity(), cursor, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) rootView.findViewById(R.id.Task_list);
        mListView.setAdapter(mTaskAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                TextView textView = (TextView) view.findViewById(R.id.list_item_title);
                String title = textView.getText().toString();
                Intent intent = new Intent(getActivity(), TaskDetail.class);
                intent.putExtra(EXTRA_MESSAGE, title);
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cursor.requery();
        mTaskAdapter.notifyDataSetChanged();
    }
}
