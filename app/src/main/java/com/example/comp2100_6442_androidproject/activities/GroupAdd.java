package com.example.comp2100_6442_androidproject.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.utils.ConnectionTemplate;
import com.example.comp2100_6442_androidproject.utils.ProgressDialogTemplate;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GroupAdd extends AppCompatActivity {
    private static final String TAG = "GroupAdd page";
    String searchResultUsersJSON;
    String emailString;
    EditText groupName;
    EditText description;
    TextView textView;

    boolean isSaveSuccessful=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        //action bar on right top
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        //initialize fields
        groupName = findViewById(R.id.mTimeEdit);
        description = findViewById(R.id.mLenEdit);

        textView = (TextView) findViewById(R.id.selectedUsersTextView);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);

        searchResultUsersJSON = "";
        //initialize the emailString
        SharedPreferences sharedPreferences =getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
        emailString= sharedPreferences.getString("email","");
        //searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUser(query);
                final ProgressDialog progressDialog = ProgressDialogTemplate.showDialog(GroupAdd.this, "Searching...");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {// delay 5 millis and then run this
                        progressDialog.dismiss();
                        if ("[]".equals(searchResultUsersJSON) || "".equals(searchResultUsersJSON)) {
                            Toast.makeText(GroupAdd.this, "do not find", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(GroupAdd.this,UserList.class);

                            Bundle bundle = new Bundle();
                            bundle.putString("searchResultUsersJSON",searchResultUsersJSON);
                            intent.putExtras(bundle);
                            startActivityForResult(intent,932);
                        }
                    }
                    //waiting seconds
                }, 3500);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //receive the result from UserList
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==932&&resultCode==932){
            Bundle bundle = data.getExtras();
            String selectedEmail = bundle.getString("selectedEmail");
            if (!textView.getText().toString().contains(selectedEmail)){
                textView.setText(textView.getText().toString()+"\n"+selectedEmail);
                emailString= emailString+"~~"+selectedEmail;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    //When click the save button
    public void addGroupSave(View view) {

        if (groupName.getText().toString().isEmpty() || description.getText().toString().isEmpty()) {
            Toast.makeText(this, "please fill in the blanks", Toast.LENGTH_SHORT).show();
            return;
        }
        saveGroup();

        final ProgressDialog progressDialog = ProgressDialogTemplate.showDialog(GroupAdd.this, "Adding...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();
                if (isSaveSuccessful) {
                    finish();
                }else {
                    Toast.makeText(GroupAdd.this, "failed, please click again", Toast.LENGTH_SHORT).show();
                }
            }
            //waiting seconds
        }, 3500);
    }

    //network communication
    private void saveGroup() {
        String parameter = "?groupName=" + groupName.getText().toString().trim()
                + "&description=" + description.getText().toString().trim()
                +"&emailString="+emailString;

        Call task = ConnectionTemplate.getConnection("/groupAdd", parameter);
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
                    String responseString=body.string();
                    Log.d(TAG, "body: " + responseString);
                    if ("1".equals(responseString)){
                        isSaveSuccessful=true;
                    }

                }
            }
        });
    }
    //search user network communication
    private void searchUser(String contents) {
        String parameter = "?searchContents=" + contents;

        Call task = ConnectionTemplate.getConnection("/searchUser", parameter);
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
                    searchResultUsersJSON = body.string();
                    Log.d(TAG, "body: " + searchResultUsersJSON);

                }
            }
        });
    }
}
