package com.beeminder.gtbee;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.beeminder.gtbee.adapters.FailedTaskAdapter;
import com.beeminder.gtbee.data.Contract;


/**
 * A placeholder fragment containing a simple view.
 */
public class FailedTaskActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int UNIQUE_LOADER_FAILED_TASK_FRAGMENT = 2;
    public FailedTaskAdapter mTaskAdapter;
    private ListView mListView;

    private Cursor cursor;

    public FailedTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTaskAdapter = new FailedTaskAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_failed_task, container, false);

        mListView = (ListView) rootView.findViewById(R.id.failed_task_list);
        mListView.setAdapter(mTaskAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Log.v("TaskFragment", "l: " + Long.toString(l));
//
//                Intent intent = new Intent(getActivity(), TaskDetail.class);
//                intent.putExtra(TaskDetail.KEY_ID, l);
//                startActivity(intent);
//            }
//        });


        getLoaderManager().initLoader(UNIQUE_LOADER_FAILED_TASK_FRAGMENT, null, this);
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Contract.FAILED_TASKS_URI, null, null, null, Contract.KEY_DUE_DATE + " ASC");
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
