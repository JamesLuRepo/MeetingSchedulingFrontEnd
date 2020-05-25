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

    public void SetPre(View v) {
        Intent intent = new Intent(this, TimeSlotInfo.class);
        startActivity(intent);
    }

    public void LogOut(View v) {
        Intent intent = new Intent(this, LogInPage.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


}
