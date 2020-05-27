package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import com.example.comp2100_6442_androidproject.utils.ConnectionTemplate;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

public class MeetingAdd extends AppCompatActivity {
    private String TAG = "MeetingAdd: ";
    EditText name;
    EditText notes;
    TextView holdTime;
    EditText duration;
    EditText location;
    TextView deadline;
    Calendar calendar;

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

    //initialize the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_add);
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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        gpsId = bundle.getString("gpsId", "");

        gson = new Gson();
    }
    //upper left arraw
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    //setHold time
    public void setHoldTime(View view) {
        holdTimeString = "";
        new TimePickerDialog(MeetingAdd.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour = hourOfDay + "";
                String min = minute + "";
                if (hourOfDay < 10) {
                    hour = "0" + hour;
                }
                if (minute < 10) {
                    min = "0" + min;
                }
                holdTimeString = holdTimeString + hour + ":" + min + ":" + "00";
                holdTime.setText(holdTimeString);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        new DatePickerDialog(MeetingAdd.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month = monthOfYear + "";
                String day = dayOfMonth + "";
                if (monthOfYear < 10) {
                    month = "0" + month;
                }
                if (dayOfMonth < 10) {
                    day = "0" + day;
                }
                holdTimeString = year + "-" + month + "-" + day + " ";
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    //set deadline time
    public void setDeadlineTime(View view) {
        deadlineString = "";
        new TimePickerDialog(MeetingAdd.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour = hourOfDay + "";
                String min = minute + "";
                if (hourOfDay < 10) {
                    hour = "0" + hour;
                }
                if (minute < 10) {
                    min = "0" + min;
                }

                deadlineString = deadlineString + hour + ":" + min + ":" + "00";

                deadline.setText(deadlineString);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        new DatePickerDialog(MeetingAdd.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month = monthOfYear + "";
                String day = dayOfMonth + "";
                if (monthOfYear < 10) {
                    month = "0" + month;
                }
                if (dayOfMonth < 10) {
                    day = "0" + day;
                }
                deadlineString = year + "-" + month + "-" + day + " ";
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
            Toast.makeText(this, "Please fill in the blank!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (holdTimeString.isEmpty() || deadlineString.isEmpty()) {
            Toast.makeText(this, "Please choose time!", Toast.LENGTH_SHORT).show();
            return;
        }
        Meeting meeting = new Meeting(null,
                nameString,
                notesString,
                holdTimeString,
                Integer.parseInt(durationString),
                locationString,
                deadlineString,
                Integer.parseInt(gpsId));
        String meetingJson = gson.toJson(meeting);
        isSaveSuccessful = false;
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
                    finish();
                } else {
                    Toast.makeText(MeetingAdd.this, "failed please click again", Toast.LENGTH_SHORT).show();
                }
            }
            //waiting seconds
        }, 5000);
    }

    // network connection
    public void postMeetingInfo(String meetingJson) {

        Call task = ConnectionTemplate.postConnection("/meetingAdd", meetingJson);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                if (code == HttpsURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String s = body.string();
                        isSaveSuccessful = true;
                    }
                }
            }
        });
    }
}
