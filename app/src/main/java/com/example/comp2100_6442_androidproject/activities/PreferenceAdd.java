package com.example.comp2100_6442_androidproject.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;

import java.util.Calendar;

public class PreferenceAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_add);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
         //   actionBar.setHomeButtonEnabled(true);
        }
        Button button = (Button)findViewById(R.id.dateSet);
        button.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v){
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(PreferenceAdd.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Toast.makeText(PreferenceAdd.this,"You chose : "+year+"."+month+"."+dayOfMonth,Toast.LENGTH_SHORT).show();

                            }
                        }
                        ,calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        Button button1 = (Button)findViewById(R.id.timeSet);
        button1.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v1){
                Calendar c = Calendar.getInstance();
                new TimePickerDialog(PreferenceAdd.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Toast.makeText(PreferenceAdd.this,"You chose : "+hourOfDay+":"+minute,Toast.LENGTH_SHORT).show();
                            }
                        }
                        , c.get(Calendar.HOUR_OF_DAY)
                        , c.get(Calendar.MINUTE)
                        , true).show();

            }
        });

    }
    }

