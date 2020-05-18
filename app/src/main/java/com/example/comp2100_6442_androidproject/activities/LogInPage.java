package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.utils.ConnectionTemplate;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LogInPage extends AppCompatActivity {
    private static final String TAG = "logInPage";
    //exitTime
    private  long exitTime=0;


    EditText emailEditText;
    EditText passwordEditText;

    String logInResult;//"successful" if successful; "fail" if fail

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);
        this.emailEditText = findViewById(R.id.LogInEmail);
        this.passwordEditText = findViewById(R.id.LogInPassword);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
    public void exit(){
        if ((System.currentTimeMillis()-exitTime)>2000){
            Toast.makeText(getApplicationContext(),"click again to exit",Toast.LENGTH_SHORT).show();
            exitTime=System.currentTimeMillis();
        }else{
            moveTaskToBack(true);
        }
    }

    // execute this part of code when click on Log in button
    public void logInButton(View v) {
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
                if ("successful".equals(logInResult)) {
                    SharedPreferences.Editor editor = getSharedPreferences("emailDataBase",MODE_PRIVATE).edit();
                    editor.putString("email",emailEditText.getText().toString().trim());
                    editor.commit();
                    finish();
                } else {
                    Toast.makeText(LogInPage.this, "Your email or password does not exist", Toast.LENGTH_SHORT).show();


                }
            }
            //waiting seconds
        }, 3500);
    }

    // execute this part of code when click on Sign up button in main page
    public void signUpButton(View v) {
        Intent intent = new Intent(LogInPage.this, SignUpPage.class);
        startActivity(intent);

    }

    private void logIn() {

        // check the blank
        if (emailEditText.getText().toString().trim().isEmpty()
                || passwordEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "please fill in the blanks", Toast.LENGTH_SHORT).show();
            return;
        }

        String parameter = "?email=" + emailEditText.getText().toString().trim()
                + "&password=" + passwordEditText.getText().toString().trim();

        Call task = ConnectionTemplate.getConnection("/signIn", parameter);
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
