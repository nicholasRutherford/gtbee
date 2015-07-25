package com.beeminder.gtbee;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.beeminder.gtbee.data.Contract;


/**
 * A placeholder fragment containing a simple view.
 */
public class TaskFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final int UNIQUE_LOADER_TASK_FRAGMENT = 1;
    public TaskAdapter mTaskAdapter;
    private ListView mListView;
    public final static String EXTRA_MESSAGE = "com.beeminder.gtbee.TITLE_MESSAGE";
    private Cursor cursor;

    public TaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTaskAdapter = new TaskAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) rootView.findViewById(R.id.Task_list);
        mListView.setAdapter(mTaskAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.v("TaskFragment", "id: " + Integer.toString(view.getId()));
                Log.v("TaskFragment", "l: " + Long.toString(l));

                TextView textView = (TextView) view.findViewById(R.id.list_item_title);
                String title = textView.getText().toString();
                Intent intent = new Intent(getActivity(), TaskDetail.class);
                intent.putExtra(EXTRA_MESSAGE, title);
                startActivity(intent);
            }
        });


        getLoaderManager().initLoader(UNIQUE_LOADER_TASK_FRAGMENT, null, this);
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Contract.ACTIVE_TASKS_URI, null, null, null, Contract.KEY_DUE_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTaskAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTaskAdapter.swapCursor(null);

    }
}
