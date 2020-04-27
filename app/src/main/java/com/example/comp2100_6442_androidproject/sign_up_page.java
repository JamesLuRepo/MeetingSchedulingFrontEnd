package com.example.comp2100_6442_androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class sign_up_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
    }

    public void backButton(View v){
//        Intent intent = new Intent(NewActivity.this, MainActivity.class);
//        startActivity(intent);
        finish();

    }

    public void BackHome(View v){
        finish();
    }

    public void SendVerificationCode(View v){
        Toast toast = Toast.makeText(getApplicationContext(), "The verification code has been sent to your email, please check.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void Sign_up_successful_button(View v){
        Toast toast = Toast.makeText(getApplicationContext(), "you have already Sign up!", Toast.LENGTH_SHORT);
        toast.show();
    }

}
