package com.beeminder.gtbee;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.beeminder.gtbee.data.TaskDbHelper;

import java.util.Calendar;


public class TaskDetail extends ActionBarActivity {
    public String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(TaskFragment.EXTRA_MESSAGE);

        SQLiteDatabase db = new TaskDbHelper(this).getWritableDatabase();
        Cursor cur = db.query(TaskDbHelper.TABLE_NAME,
                null,
                "title=\"" + mTitle + "\"",
                null, null, null, null);

        if (cur.getCount()==0){
            finish();
        } else {
            cur.moveToFirst();

            Long dateDue = cur.getLong(TaskDbHelper.COL_DUE_DATE);
            Long currentDate = Calendar.getInstance().getTimeInMillis();

            if ((dateDue - currentDate) < 0){
                setContentView(R.layout.activity_task_detail_past);
            } else{
                setContentView(R.layout.activity_task_detail);
            }

            getSupportActionBar().setTitle(mTitle);
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
        new Utility().deleteTaskFromTitle(mTitle, this);
        finish();
    }


    public void renewTask(View view){

        SQLiteDatabase db = new TaskDbHelper(this).getWritableDatabase();
        Cursor cur = db.query(TaskDbHelper.TABLE_NAME,
                null,
                "title=\"" + mTitle + "\"",
                null, null, null, null);
        cur.moveToFirst();
        int retryNumeber = cur.getInt(TaskDbHelper.COL_RETRY);

        Intent intent = new Intent(this, NewTask.class);
        intent.putExtra(NewTask.TASK_NAME, mTitle);
        intent.putExtra(NewTask.RETRY_NUMBER, retryNumeber + 1);
        startActivity(intent);
        finish();

    }
}
