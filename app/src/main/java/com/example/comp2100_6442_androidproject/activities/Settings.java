package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.comp2100_6442_androidproject.R;

public class Settings extends AppCompatActivity {

    String userNameString;
    TextView userName;

    //initialize the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        //   actionBar.setDisplayHomeAsUpEnabled(true);
        userName= findViewById(R.id.textView2);
        SharedPreferences sp = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
        userNameString = sp.getString("userName", "");
        userName.setText(userNameString);


    }
    // when click View Time Slots
    public void SetPre(View v) {
        Intent intent = new Intent(this, TimeSlotInfo.class);
        startActivity(intent);
    }
    // when click the Log Out
    public void LogOut(View v) {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
    //upper left arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
