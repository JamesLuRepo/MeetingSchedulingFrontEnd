package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.User;

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

public class SignUpPage extends AppCompatActivity {
    private static final String TAG = "SignUpPage";
    public static final String BASE_URL = "http://49.234.105.82:8080";

    EditText emailAddressEditText;
    EditText verificationCodeEditText;
    EditText nameEditText;
    EditText passwordEditText;

    String correctVerificationCode;

    Boolean isMailExist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        emailAddressEditText = findViewById(R.id.signUpEmailAddress);
        verificationCodeEditText = findViewById(R.id.editText4);
        nameEditText = findViewById(R.id.AccountName);
        passwordEditText = findViewById(R.id.editText2);
    }

    public void backButton(View v) {
//        Intent intent = new Intent(NewActivity.this, MainActivity.class);
//        startActivity(intent);
        finish();
    }


    // execute this part of code when click on Back Home
    public void BackHome(View v) {
        finish();
    }

    // execute this part of code when click on Send Verification Code
    public void SendVerificationCode(View v) {
        emailAddressEditText = findViewById(R.id.signUpEmailAddress);
        int r = (int) (1000 + Math.random() * 8999);
        correctVerificationCode = r + "";
        String parameter = "?email=" + emailAddressEditText.getText().toString().trim()
                + "&verificationCode=" + correctVerificationCode;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(59000, TimeUnit.MILLISECONDS)
                .readTimeout(59000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + "/ms/sendVerificationCode" + parameter)
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
                    String s = body.string();
                    isMailExist = s.trim().equals("1");
                    Log.d(TAG, "body: " + s);
                    Log.d(TAG, "onResponse: ismailexist"+isMailExist);
                }
            }
        });

        if (emailAddressEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "please fill in the blanks", Toast.LENGTH_SHORT).show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "The verification code has been sent to your email, please check.", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    // execute this part of code when click on Sign up button in sign up page
    public void Sign_up_successful_button(View v) {

        String email = emailAddressEditText.getText().toString().trim();
        String verificationCode = verificationCodeEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if ("".equals(email) || "".equals(name) ||"".equals(verificationCode)|| "".equals(password)) {
            Toast.makeText(this, "please fill in the blanks", Toast.LENGTH_SHORT).show();
        } else
        if (!verificationCode.equals(correctVerificationCode)) {
            Toast.makeText(this, "please input the correct verification code", Toast.LENGTH_SHORT).show();
        } else
            if (isMailExist) {
            Toast.makeText(this, "the email has existed", Toast.LENGTH_SHORT).show();
        } else {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10000, TimeUnit.MILLISECONDS)
                    .build();

            String parameter = "?email=" + email
                    + "&name=" + name
                    + "&password=" + password;
            Request request = new Request.Builder()
                    .get()
                    .url(BASE_URL + "/ms/signUp" + parameter)
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
                        Log.d(TAG, "body: " + body.string());
                    }
                }
            });
            Toast toast = Toast.makeText(getApplicationContext(), "you have already Sign up!", Toast.LENGTH_SHORT);
            toast.show();
            this.backButton(null);
        }

    }
}
