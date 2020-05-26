package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.Meeting;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ModifyMeeting extends AppCompatActivity {

    private String TAG = "ModifyMeeting: ";
    EditText name;
    EditText notes;
    TextView holdTime;
    EditText duration;
    EditText location;
    TextView deadline;
    Calendar calendar;

    String mIdString;
    String nameString;
    String notesString;
    String holdTimeString;
    String ausHoldTimeString;
    String durationString;
    String locationString;
    String deadlineString;
    String ausDeadlineString;
    String gpsId;

    Gson gson;

    boolean isSaveSuccessful = false;

    Meeting meeting;

    //initialize this activities
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_meeting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        name = findViewById(R.id.add_meeting_name);
        notes = findViewById(R.id.add_meeting_notes);
        holdTime = findViewById(R.id.add_meeting_time);
        duration = findViewById(R.id.add_meeting_duration);
        location = findViewById(R.id.add_meeting_location);
        deadline = findViewById(R.id.add_meeting_deadline);
        calendar = Calendar.getInstance();

        nameString = "";
        notesString = "";
        holdTimeString = "";
        ausHoldTimeString = "";
        durationString = "";
        locationString = "";
        deadlineString = "";
        ausDeadlineString = "";

        SharedPreferences sp = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
        mIdString= sp.getString("meetingMId","");
        nameString = sp.getString("meetingName", "");
        notesString = sp.getString("meetingNotes", "");
        holdTimeString = sp.getString("meetingHoldTime", "");
        durationString = sp.getString("meetingTimeLength", "");
        locationString = sp.getString("meetingLocation", "");
        deadlineString = sp.getString("meetingScheduling_ddl", "");
        gpsId = sp.getString("meetingGId", "");

        name.setText(nameString);
        notes.setText(notesString);
        holdTime.setText(holdTimeString);
        duration.setText(durationString);
        location.setText(locationString);
        deadline.setText(deadlineString);

        gson = new Gson();
    }
    //upper left arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    //set hold time
    public void setHoldTime(View view) {


        new TimePickerDialog(ModifyMeeting.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                holdTimeString = holdTimeString + hourOfDay + ":" + minute + ":" + "00";
                ausHoldTimeString = ausHoldTimeString + hourOfDay + ":" + minute;
                holdTime.setText(ausHoldTimeString);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        new DatePickerDialog(ModifyMeeting.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                holdTimeString = year + "-" + monthOfYear + "-" + dayOfMonth + " ";
                ausHoldTimeString = dayOfMonth + "." + monthOfYear + "." + year + " ";
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    //set deadline
    public void setDeadlineTime(View view) {


        new TimePickerDialog(ModifyMeeting.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                deadlineString = deadlineString + hourOfDay + ":" + minute + ":" + "00";
                ausDeadlineString = ausDeadlineString + hourOfDay + ":" + minute;
                deadline.setText(ausDeadlineString);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        new DatePickerDialog(ModifyMeeting.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                deadlineString = year + "-" + monthOfYear + "-" + dayOfMonth + " ";
                ausDeadlineString = dayOfMonth + "." + monthOfYear + "." + year + " ";
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    //when click the save button
    public void saveMeeting(View view) {
        nameString = name.getText().toString().trim();
        notesString = notes.getText().toString().trim();
        durationString = duration.getText().toString().trim();
        locationString = location.getText().toString().trim();

        if (nameString.isEmpty() || notesString.isEmpty() || durationString.isEmpty() || locationString.isEmpty()) {
            Toast.makeText(this, "please fill in the blank", Toast.LENGTH_SHORT).show();
            return;
        }
         meeting = new Meeting(Integer.parseInt(mIdString),
                nameString,
                notesString,
                holdTimeString,
                Integer.parseInt(durationString),
                locationString,
                deadlineString,
                Integer.parseInt(gpsId));
        String meetingJson = gson.toJson(meeting);
        isSaveSuccessful=false;
        postMeetingInfo(meetingJson);


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this


                progressDialog.dismiss();
                //dismiss the dialog and Toast or switch activity
                if (isSaveSuccessful) {
                    SharedPreferences.Editor editor = getSharedPreferences("localDataBase",MODE_PRIVATE).edit();
                    //When click one schedule, the the meeting information will be saved into the local database
                    editor.putString("meetingName",meeting.getName());
                    editor.putString("meetingNotes",meeting.getNotes());
                    editor.putString("meetingHoldTime",meeting.getHoldTime());
                    editor.putString("meetingTimeLength",meeting.getTimeLength()+"");
                    editor.putString("meetingLocation",meeting.getLocation());
                    editor.putString("meetingScheduling_ddl",meeting.getScheduling_ddl());
                    editor.putString("meetingMId",meeting.getMid()+"");
                    editor.putString("meetingGId",meeting.getGpsGid()+"");
                    editor.commit();







                    finish();
                } else {
                    Toast.makeText(ModifyMeeting.this, "failed please click again", Toast.LENGTH_SHORT).show();
                }
            }
            //waiting seconds
        }, 5000);
    }
    //network connection
    public void postMeetingInfo(String meetingJson) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .build();


        MediaType mediaType = MediaType.parse("application/json");

        RequestBody requestBody = RequestBody.create(meetingJson, mediaType);

        Request request = new Request.Builder()
                .post(requestBody)
                .url("http://49.234.105.82:8080/ms" + "/meetingModify")
                .build();

        Call task = client.newCall(request);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code:" + code);
                if (code == HttpsURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String s = body.string();
                        isSaveSuccessful = true;
                        Log.d(TAG, "result: " +s);
                    }
                }
            }
        });
    }
}
