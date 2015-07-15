package com.beeminder.gtbee;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.beeminder.gtbee.data.TaskDbHelper;
import com.beeminder.gtbee.integrations.BeeminederIntActivity;
import com.beeminder.gtbee.services.BeeminederIntSendDataService;
import com.beeminder.gtbee.services.PaymentService;
import com.beeminder.gtbee.services.ReminderService;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


public class NewTask extends ActionBarActivity implements TimePickerDialog.OnTimeSetListener,
        NewTaskFragment.DateListener,
        DatePickerDialog.OnDateSetListener{
    public long mdate;
    public String oldTaskName;
    public int retryNumber;
    public int mPenalty = 0;

    public static final String TASK_NAME = "com.beeminder.gtbee.task_name";
    public static final String RETRY_NUMBER = "com.beeminder.gtbee.retry_number";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        oldTaskName = intent.getStringExtra(TASK_NAME);
        retryNumber = intent.getIntExtra(RETRY_NUMBER, 0);


        Log.v("Initial time", Long.toString(mdate));
        if (mdate == 0){

            Calendar rightNow = Calendar.getInstance();
            mdate = rightNow.getTimeInMillis();
            Log.v("New", "Set to local time: " + new Utility().niceDateTime(mdate));
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
        Log.v("time", "hour: " + Integer.toString(hourOfDay) + ", min: " + Integer.toString(minute));

        Date date = new Date(mdate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        mdate = cal.getTimeInMillis();
        Log.v("Date", "New Date: " + Long.toString(mdate));

        NewTaskFragment frag = (NewTaskFragment) getSupportFragmentManager().findFragmentById(R.id.new_task_fragment);
        View Fragview = frag.getView();
        TextView textView = (TextView) Fragview.findViewById(R.id.new_task_due_date);
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
        Log.v("Date", "New Date: " + Long.toString(mdate));

        NewTaskFragment frag = (NewTaskFragment) getSupportFragmentManager().findFragmentById(R.id.new_task_fragment);
        View Fragview = frag.getView();
        TextView textView = (TextView) Fragview.findViewById(R.id.new_task_due_date);
        textView.setText(new Utility().niceDateTime(mdate));
    }

    public void checkTask(){
        Long dueDate = mdate;
        String title;

        Long currentTime = Calendar.getInstance().getTimeInMillis() + 60 * 1000;

        if ((dueDate - currentTime) < 0){
            Toast t = new Toast(this).makeText(this, "Due date must be after now.", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int customPenaltyAdjuster= Integer.parseInt(sharedPref.getString("starting_amount_list_pref", "0"));
        mPenalty= new Utility().retryToAmount(retryNumber + customPenaltyAdjuster);

        // Check if the freebie button is on
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.new_task_freebie_toggle);
        if (toggleButton.isChecked()){
            mPenalty = 0;
        }

        NewTaskFragment frag = (NewTaskFragment) getSupportFragmentManager().findFragmentById(R.id.new_task_fragment);
        View view = frag.getView();
        EditText editText= (EditText) view.findViewById(R.id.new_task_title);
        title = editText.getText().toString().trim();

        // Any Task starting with test will have no penalty
        if (Pattern.matches("(t|T)est\\d?.*", title)){
            mPenalty = 0;
        }

        //Verify task with user
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

        NewTaskFragment frag = (NewTaskFragment) getSupportFragmentManager().findFragmentById(R.id.new_task_fragment);
        View view = frag.getView();
        EditText editText= (EditText) view.findViewById(R.id.new_task_title);
        String title = editText.getText().toString().trim();

        Long addedDate = Calendar.getInstance().getTimeInMillis();

        // Check if the freebie button is on
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.new_task_freebie_toggle);
        // Reduce their number of freebies available
        if (toggleButton.isChecked()){

            SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
            int haveFree = settings.getInt(OauthActivity.PREF_FREEBIES,0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(OauthActivity.PREF_FREEBIES, haveFree-1);
            editor.commit();
        }


        if (retryNumber > 0){
            new Utility().deleteTaskFromTitle(title, this);
        }
        Context context = getApplicationContext();
        SQLiteDatabase db = new TaskDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.COLUMN_ADDED_DATE, addedDate);
        values.put(TaskDbHelper.COLUMN_TITLE, title);
        values.put(TaskDbHelper.COLUMN_DUE_DATE, mdate);
        values.put(TaskDbHelper.COLUMN_PENALTY, mPenalty);
        values.put(TaskDbHelper.COLUMN_RETRY_COUNT, retryNumber);

        db.insert(TaskDbHelper.TABLE_NAME, null, values);


        setNotifications(title, mdate, mPenalty);

        this.finish();

    }

    private void setNotifications(String title, Long date, int penalty){
        int base_id;
        int hour_id;
        int day_id;
        int pay_id;

        int hourMili = 60*60*1000;
        int dayMili = 24*hourMili;

        SQLiteDatabase db = new TaskDbHelper(getApplicationContext()).getWritableDatabase();
        Cursor cur = db.query(TaskDbHelper.TABLE_NAME,
                new String[]{"_ID"},
                "title=\"" + title + "\"",
                null, null, null, null);
        cur.moveToFirst();
        base_id = cur.getInt(0);
        hour_id = base_id * 100 + 60; // 60 min in an hour
        day_id = base_id * 100 + 24; // 24 hours in a day
        pay_id = base_id * 100 + 55; // 55 = $$

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        // One hour notification
        Intent intentHour = new Intent(this, ReminderService.class);
        intentHour.putExtra(ReminderService.REMINDER_TITLE, title + "!");
        intentHour.putExtra(ReminderService.REMINDER_TEXT, "Due in less than one hour! Eek!");
        intentHour.putExtra(ReminderService.REMINDER_ID, hour_id);
        intentHour.putExtra(ReminderService.TASK_TITLE, title);

        PendingIntent pendingIntentHour = PendingIntent.getService(this, hour_id, intentHour, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.v("newTask", "Due Date: " + new Utility().niceDateTime(mdate));

        alarmManager.set(AlarmManager.RTC_WAKEUP, mdate - hourMili, pendingIntentHour);
        Log.v("newTask", "Alarm set for: " + new Utility().niceDateTime(mdate - hourMili));


        // One day notification
        Intent intentDay = new Intent(this, ReminderService.class);
        intentDay.putExtra(ReminderService.REMINDER_TITLE, title + "!");
        intentDay.putExtra(ReminderService.REMINDER_TEXT, "Due in less than one day.");
        intentDay.putExtra(ReminderService.REMINDER_ID, day_id);
        intentDay.putExtra(ReminderService.TASK_TITLE, title);

        PendingIntent pendingIntentDay = PendingIntent.getService(this, day_id, intentDay, PendingIntent.FLAG_UPDATE_CURRENT);


        alarmManager.set(AlarmManager.RTC_WAKEUP, mdate - dayMili, pendingIntentDay);
        Log.v("newTask", "Alarm set for: " + new Utility().niceDateTime(mdate - dayMili));

        // Set payment alarm
        if (penalty > 0 ) {
            Intent intentPayment = new Intent(this, PaymentService.class);
            intentPayment.putExtra(PaymentService.TASK_TITLE, title);
            intentPayment.putExtra(PaymentService.TASK_ID, base_id);
            intentPayment.putExtra(PaymentService.ATTEMPT_NUMBER, 0);
            intentPayment.putExtra(PaymentService.PAYMENT_AMOUNT, penalty);

            PendingIntent pendingIntentPayment = PendingIntent.getService(this, pay_id, intentPayment, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, mdate, pendingIntentPayment);
            Log.v("newTask", "Payment set for: " + new Utility().niceDateTime(mdate));
        }

        SharedPreferences settings = getSharedPreferences(OauthActivity.PREF_NAME, MODE_PRIVATE);
        String beeminderGoal = settings.getString(BeeminederIntActivity.BEEMINDER_GOAL, null);

        if (!(beeminderGoal == null)){
            Intent intentSendData = new Intent(this, BeeminederIntSendDataService.class);
            intentSendData.putExtra(BeeminederIntSendDataService.TASK_TITLE, title);
            intentSendData.putExtra(BeeminederIntSendDataService.TASK_ID, base_id);
            intentSendData.putExtra(BeeminederIntSendDataService.ATTEMPT_NUMBER, 0);

            startService(intentSendData);
            Log.v("NewTask", "Send datapoint to beeminder!");
        }

    }

}
