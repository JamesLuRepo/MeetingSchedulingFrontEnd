package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mainActivity";
    //do not use localhost, because the default localhost is 127.0.0.1 which is not correct
    public static final String BASE_URL = "http://49.234.105.82:8080"; // on the cloud server
//    public static final String BASE_URL = "http://192.168.1.100:8080";//local for test

    EditText emailEditText;
    EditText passwordEditText;

    String logInResult;//"successful" if successful; "fail" if fail

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.emailEditText = findViewById(R.id.LogInEmail);
        this.passwordEditText = findViewById(R.id.LogInPassword);
    }

    // execute this part of code when click on Sign up button in main page
    public void Sign_up_button(View v) {
        Intent intent = new Intent(MainActivity.this, SignUpPage.class);
        startActivity(intent);

    }

    // execute this part of code when click on Log in button
    public void Log_in_button(View v) {
        //connect to the server
        logIn();
        // the progressDialog to wait for the respond
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();
                //dismiss the dialog and Toast or switch activity
                if (logInResult.equals("successful")) {
                    Intent intent = new Intent(MainActivity.this, DashBoard.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Your email or password does not exist", Toast.LENGTH_SHORT).show();
                }
            }
            //waiting seconds
        }, 3500);
    }


    private void logIn() {

        // check the blank
        if (emailEditText.getText().toString().trim().isEmpty()
                || passwordEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "please fill in the blanks", Toast.LENGTH_SHORT).show();
            return;
        }

        //the following are connections
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .build();

        String parameter = "?email=" + emailEditText.getText().toString().trim()
                + "&password=" + passwordEditText.getText().toString().trim();

        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + "/ms/signIn" + parameter)
                .build();

        Call task = okHttpClient.newCall(request);

        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code: " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();
                    //save the log in result to decide what to do next
                    logInResult = body.string();
                    Log.d(TAG, "body: " + logInResult);


                }
            }
        });


    }


}
