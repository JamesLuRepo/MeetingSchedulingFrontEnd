package com.example.comp2100_6442_androidproject.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.Preference;
import com.example.comp2100_6442_androidproject.domain.ScheduleShow;
import com.example.comp2100_6442_androidproject.utils.ConnectionTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

public class TimeSlotAdd extends AppCompatActivity {
    private static final String TAG = "TimeSlotAdd: ";
    TextView showStartTime;
    TextView showEndTime;

    String startTimeString;
    String endTimeString;

    Calendar calendar;
    Gson gson;
    private boolean isSaveSuccessful;

    //initialize the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot_add);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        showStartTime = findViewById(R.id.showStartTime);
        showEndTime = findViewById(R.id.showEndTime);

        startTimeString = "";
        endTimeString = "";
        calendar = Calendar.getInstance();
        gson = new Gson();
        isSaveSuccessful=false;

    }
    //upper left arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    //set start time
    public void setStartTime(View view) {
        startTimeString = "";

        new TimePickerDialog(TimeSlotAdd.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour = hourOfDay+"";
                String min= minute+"";
                if (hourOfDay<10){
                    hour="0"+hour;
                }
                if(minute<10){
                    min="0"+min;
                }

                startTimeString = startTimeString + hour + ":" + min + ":" + "00";
                showStartTime.setText(startTimeString);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        new DatePickerDialog(TimeSlotAdd.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear ++;
                String month=monthOfYear+"";
                String day = dayOfMonth+"";
                if(monthOfYear<10){
                    month="0"+month;
                }
                if(dayOfMonth<10){
                    day="0"+day;
                }

                startTimeString = year + "-" + month + "-" + day + " ";
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    //set end time
    public void setEndTime(View view) {
        endTimeString = "";
        new TimePickerDialog(TimeSlotAdd.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour = hourOfDay+"";
                String min= minute+"";
                if (hourOfDay<10){
                    hour="0"+hour;
                }
                if(minute<10){
                    min="0"+min;
                }
                endTimeString = endTimeString + hour + ":" + min + ":" + "00";
                showEndTime.setText(endTimeString);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        new DatePickerDialog(TimeSlotAdd.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month=monthOfYear+"";
                String day = dayOfMonth+"";
                if(monthOfYear<10){
                    month="0"+month;
                }
                if(dayOfMonth<10){
                    day="0"+day;
                }
                endTimeString = year + "-" + month + "-" + day + " ";
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    // when click SAVE
    public void savePreference(View view) {
        isSaveSuccessful=false;



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date startDate = null;
        Date endDate = null;

        try {
            startDate=sdf.parse(startTimeString);
            endDate=sdf.parse(endTimeString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startMillisecond = startDate.getTime();
        long endMillisecond = endDate.getTime();
        if (startMillisecond>endMillisecond){
            Toast.makeText(this, "Your time setting is illegal, please reset!", Toast.LENGTH_SHORT).show();
            return;
        }

        addPreference();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();

                if (isSaveSuccessful){
                    finish();
                }else{
                    Toast.makeText(TimeSlotAdd.this, "network is slow, please try again", Toast.LENGTH_SHORT).show();
                }
            }
            //waiting seconds
        }, 5000);

    }
    //network connection
    private void addPreference() {
        SharedPreferences sp = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
        String email = sp.getString("email", "");
        String parameter = "?email=" + email
                + "&startTime" + startTimeString
                + "&endTime" + endTimeString;
        String preferenceJson = gson.toJson(new Preference(null, startTimeString, endTimeString, email));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .build();


        MediaType mediaType = MediaType.parse("application/json");

        RequestBody requestBody = RequestBody.create(preferenceJson, mediaType);

        Request request = new Request.Builder()
                .post(requestBody)
                .url("http://49.234.105.82:8080/ms" + "/preferenceAdd")
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

