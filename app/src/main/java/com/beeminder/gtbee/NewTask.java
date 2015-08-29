package com.beeminder.gtbee;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.beeminder.gtbee.auth.OauthActivity;
import com.beeminder.gtbee.data.Contract;
import com.beeminder.gtbee.integrations.BeeminederIntActivity;
import com.beeminder.gtbee.services.BeeminederIntSendDataService;
import com.beeminder.gtbee.services.OverdueService;
import com.beeminder.gtbee.services.ReminderService;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


public class NewTask extends ActionBarActivity implements TimePickerDialog.OnTimeSetListener,
        NewTaskFragment.DateListener,
        DatePickerDialog.OnDateSetListener{

    private final String LOG_TAG = this.getClass().getSimpleName();
    public long mdate;
    public long mOldTaskID;
    public int mPenalty = 0;
    public int mRetryNumber = 0;

    public static final String KEY_OLD_TASK_ID = "com.beeminder.gtbee.task_id";
    public static final String KEY_RETRY_NUMBER = "com.beeminder.gtbee.retry_number";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mOldTaskID = intent.getLongExtra(KEY_OLD_TASK_ID, -1l);
        mRetryNumber = intent.getIntExtra(KEY_RETRY_NUMBER, 0);

        if (mdate == 0){
            Calendar rightNow = Calendar.getInstance();
            mdate = rightNow.getTimeInMillis();
        }
        setContentView(R.layout.activity_new_task);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_task_save) {
            checkTask();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.v(LOG_TAG, "Time set - hour: " + Integer.toString(hourOfDay) + ", min: " + Integer.toString(minute));

        Date date = new Date(mdate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        mdate = cal.getTimeInMillis();
        Log.v(LOG_TAG, "New Date: " + Long.toString(mdate));

        NewTaskFragment frag = (NewTaskFragment) getSupportFragmentManager().findFragmentById(R.id.new_task_fragment);
        View fragview = frag.getView();
        TextView textView = (TextView) fragview.findViewById(R.id.new_task_due_date);
        textView.setText(new Utility().niceDateTime(mdate));
    }

    @Override
    public Long getDate() {
        return mdate;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Date date = new Date(mdate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mdate = cal.getTimeInMillis();
        Log.v(LOG_TAG, "New Date: " + Long.toString(mdate));

        NewTaskFragment frag = (NewTaskFragment) getSupportFragmentManager().findFragmentById(R.id.new_task_fragment);
        View fragview = frag.getView();
        TextView textView = (TextView) fragview.findViewById(R.id.new_task_due_date);
        textView.setText(new Utility().niceDateTime(mdate));
    }

    /**
     * Verify with the user that they want to create this new task
     */
    public void checkTask(){
        Long dueDate = mdate;
        String title;

        Long offset = 60l * 1000; // one minute offset to make the time display better
        Long currentTime = Calendar.getInstance().getTimeInMillis() + offset;

        // Cancel if the due date if before now
        if ((dueDate - currentTime) < 0){
            Toast t = new Toast(this).makeText(this, "Due date must be after now.", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        // Set the penalty based off of the users preferred starting amount, and the number of
        // times this task has been attempetd
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int customPenaltyAdjuster= Integer.parseInt(sharedPref.getString("starting_amount_list_pref", "0"));
        mPenalty= new Utility().retryToAmount(mRetryNumber + customPenaltyAdjuster);

        // Check if the freebie button is on
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.new_task_freebie_toggle);
        if (toggleButton.isChecked()){
            mPenalty = 0;
        }

        // Get the title
        NewTaskFragment frag = (NewTaskFragment) getSupportFragmentManager().findFragmentById(R.id.new_task_fragment);
        View view = frag.getView();
        EditText editText= (EditText) view.findViewById(R.id.new_task_title);
        title = editText.getText().toString().trim();

        // Any Task starting with test will have no penalty
        if (Pattern.matches("(t|T)est\\d?.*", title)){
            mPenalty = 0;
        }

        // Confirmation dialog
        if (mPenalty > 0){
            String dialogTitle = "Put yourself on the hook for $" + Integer.toString(mPenalty) + "?";
            String message = "This task will be due in " + new Utility().dialogTime(mdate);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message);
            builder.setTitle(dialogTitle);
            builder.setPositiveButton(R.string.save_task_dialog_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveTask();
                }
            });
            builder.setNegativeButton(R.string.save_task_dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            saveTask();
        }
    }

    public void saveTask(){

        // Get the title
        NewTaskFragment frag = (NewTaskFragment) getSupportFragmentManager().findFragmentById(R.id.new_task_fragment);
        View view = frag.getView();
        EditText editText= (EditText) view.findViewById(R.id.new_task_title);
        String title = editText.getText().toString().trim();

        Long addedDate = Calendar.getInstance().getTimeInMillis();

        // Check if the freebie button is on and reduce their number of freebies available
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.new_task_freebie_toggle);
        if (toggleButton.isChecked()){
            SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
            int haveFree = settings.getInt(OauthActivity.PREF_FREEBIES,0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(OauthActivity.PREF_FREEBIES, haveFree - 1);
            editor.apply();
        }


        // Creating a new task from an overdue task, so remove the old task
        if (mRetryNumber > 0){
            getContentResolver().delete(Contract.ACTIVE_TASKS_URI, Contract.KEY_ID + "=" + mOldTaskID, null);
        }

        ContentValues values = new ContentValues();
        values.put(Contract.KEY_ADDED_DATE, addedDate);
        values.put(Contract.KEY_TITLE, title);
        values.put(Contract.KEY_DUE_DATE, mdate);
        values.put(Contract.KEY_PENALTY, mPenalty);
        values.put(Contract.KEY_DESCRIPTION, "");
        values.put(Contract.KEY_RETRY_NUMBER, mRetryNumber);

        Uri insertedUri = getContentResolver().insert(Contract.ACTIVE_TASKS_URI, values);
        Cursor cur = getContentResolver().query(insertedUri, null, null, null, null);
        cur.moveToFirst();
        int id = cur.getInt(cur.getColumnIndex(Contract.KEY_ID));


        setNotifications(id, mPenalty);
        this.finish();

    }

    private void setNotifications(int id, int penalty){
        int base_id;

        int hourMili = 60*60*1000;


        Cursor cur = getContentResolver().query(Contract.ACTIVE_TASKS_URI, // Table
                new String[]{Contract.KEY_ID}, // Column
                Contract.KEY_ID + "=" + id, // Where row title is given title
                null,
                null);
        cur.moveToFirst();
        String title = cur.getString(cur.getColumnIndex(Contract.KEY_TITLE));
        base_id = cur.getInt(0);


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        // One hour notification
        ContentValues values = new ContentValues();
        values.put(Contract.KEY_TITLE, title + "!");
        values.put(Contract.KEY_DESCRIPTION, "Due in less than one hour! Eek!");
        values.put(Contract.KEY_TASK_ID, base_id);
        values.put(Contract.KEY_ALARM_TYPE, Contract.KEY_ALARM_TYPE_NOTIFICATION_ONE_TIME);
        values.put(Contract.KEY_ALARM_TIME, mdate - hourMili);
        getContentResolver().insert(Contract.ALARMS_URI, values);

        Log.v("newTask", "Due Date: " + new Utility().niceDateTime(mdate));

        // Set payment alarm
        if (penalty > 0 ) {
            values = new ContentValues();
            values.put(Contract.KEY_TITLE, title);
            values.put(Contract.KEY_DESCRIPTION, "");
            values.put(Contract.KEY_TASK_ID, base_id);
            values.put(Contract.KEY_ALARM_TYPE, Contract.KEY_ALARM_TYPE_PAYMENT);
            values.put(Contract.KEY_ALARM_TIME, mdate);

            getContentResolver().insert(Contract.ALARMS_URI, values);

        }

        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String beeminderGoal = settings.getString(BeeminederIntActivity.BEEMINDER_GOAL, null);

        if (!(beeminderGoal == null)){
            int sentStatus = 0; // Not sent

            values = new ContentValues();
            values.put(Contract.KEY_TITLE, title);
            values.put(Contract.KEY_TASK_ID, base_id);
            values.put(Contract.KEY_SENT_STATUS, 0);

            getContentResolver().insert(Contract.NETWORK_PENDING_BEEMINDER_INT_URI, values);

            Intent intentSendBeeminederInt = new Intent(this, BeeminederIntSendDataService.class);
            startService(intentSendBeeminederInt);
            Log.v("NewTask", "Send datapoint to beeminder!");
        }

    }

}
