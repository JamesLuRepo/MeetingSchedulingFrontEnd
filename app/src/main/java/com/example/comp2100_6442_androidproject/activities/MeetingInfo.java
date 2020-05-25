package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeetingInfo extends AppCompatActivity {
    private static final String TAG = "MeetingInfo:";
    TextView mId;
    TextView name;
    TextView notes;
    TextView holdTime;
    TextView duration;
    TextView location;
    TextView deadline;


    String mIdString;
    String nameString;
    String notesString;
    String holdTimeString;
    String durationString;
    String locationString;
    String deadlineString;
    String gIdString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        mId = findViewById(R.id.meetingIdTextView);
        name = findViewById(R.id.meetingNameTextView);
        notes = findViewById(R.id.meetingNotesTextView);
        holdTime = findViewById(R.id.meetingHoldTimeTextView);
        duration = findViewById(R.id.meetingDurationTextView);
        location = findViewById(R.id.meetingLocationTextView);
        deadline = findViewById(R.id.meetingDeadlineTextView);

        loadResources();
    }
    private void loadResources(){


        SharedPreferences sp = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
        mIdString = sp.getString("meetingMId", "");
        nameString = sp.getString("meetingName", "");
        notesString = sp.getString("meetingNotes", "");
        holdTimeString = sp.getString("meetingHoldTime", "");
        durationString = sp.getString("meetingTimeLength", "");
        locationString = sp.getString("meetingLocation", "");
        deadlineString = sp.getString("meetingScheduling_ddl", "");
        gIdString = sp.getString("meetingGId", "");

        Log.d(TAG, "mIdString: " + mIdString);

        mId.setText(mIdString);
        name.setText(nameString);
        notes.setText(notesString);
        holdTime.setText(holdTimeString);
        duration.setText(durationString);
        location.setText(locationString);
        deadline.setText(deadlineString);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResources();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    public void modifyMeeting(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = sdf.parse(deadlineString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millisecond = date.getTime();
        if (System.currentTimeMillis() > millisecond) {
            Toast.makeText(this, "Cannot modify, deadline was passed!", Toast.LENGTH_SHORT).show();
            
            
            return;
        }
        Intent intent = new Intent(this,ModifyMeeting.class);
        startActivity(intent);
    }


}
