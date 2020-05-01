package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;

public class DashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toast.makeText(this,"log in successfully",Toast.LENGTH_SHORT).show();
    }
}
