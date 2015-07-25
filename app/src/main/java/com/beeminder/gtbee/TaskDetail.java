package com.beeminder.gtbee;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.beeminder.gtbee.data.Contract;

import java.util.Calendar;


public class TaskDetail extends ActionBarActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public long mTaskId;

    public static final String KEY_ID = "com.beeminder.gtbee.task_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mTaskId = intent.getLongExtra(KEY_ID, 0l);
        if (mTaskId == 0l){
            Log.e(LOG_TAG, "TaskDetail recieved a task with ID 0.");
        }

        Cursor cur = getContentResolver().query(Contract.ACTIVE_TASKS_URI,
                null,
                Contract.KEY_ID + "=" + mTaskId ,
                null, null);


        if (cur.getCount()==0){
            finish();
        } else {
            cur.moveToFirst();

            Long dateDue = cur.getLong(cur.getColumnIndex(Contract.KEY_DUE_DATE));
            Long currentDate = Calendar.getInstance().getTimeInMillis();

            if ((dateDue - currentDate) < 0){
                setContentView(R.layout.activity_task_detail_past);
            } else{
                setContentView(R.layout.activity_task_detail);
            }

            String title = cur.getString(cur.getColumnIndex(Contract.KEY_TITLE));
            getSupportActionBar().setTitle(title);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View view){
        // TODO delete from ID instead of title
        getContentResolver().delete(Contract.ACTIVE_TASKS_URI, Contract.KEY_ID + "=" + mTaskId, null);
        finish();
    }


    public void renewTask(View view){
        Cursor cur = getContentResolver().query(Contract.ACTIVE_TASKS_URI, null,
                Contract.KEY_ID + "=" + mTaskId, null, null);
        cur.moveToFirst();
        int retryNumber = cur.getInt(cur.getColumnIndex(Contract.KEY_RETRY_NUMBER));

        Intent intent = new Intent(this, NewTask.class);
        intent.putExtra(NewTask.KEY_OLD_TASK_ID, mTaskId);
        intent.putExtra(NewTask.KEY_RETRY_NUMBER, retryNumber + 1);
        startActivity(intent);
        finish();

    }
}
