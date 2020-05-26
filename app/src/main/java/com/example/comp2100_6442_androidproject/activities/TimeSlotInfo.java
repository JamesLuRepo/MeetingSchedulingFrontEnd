package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.Meeting;
import com.example.comp2100_6442_androidproject.domain.Preference;
import com.example.comp2100_6442_androidproject.utils.ConnectionTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TimeSlotInfo extends AppCompatActivity {
    private static final String TAG = "TimeSlotInfo:";
    List<Preference> preferences;
    Gson gson;

    //initialize the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        preferences = new ArrayList<>();

        gson = new Gson();
        getPreferences();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();

                updateListView();
            }
            //waiting seconds
        }, 5000);
    }
    //update the List view
    private void updateListView() {
        ListView listView = (ListView) findViewById(R.id.Listview2);
        List<Map<String, Object>> listItems = new ArrayList<>();

        for (int i = 0; i < preferences.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", R.mipmap.alarm);
            map.put("start", "Start time: " + preferences.get(i).getStartTime());
            map.put("end", "End time: " + preferences.get(i).getEndTime());
            listItems.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, listItems,
                R.layout.preference_item, new String[]{"start", "icon", "end"},
                new int[]{R.id.start, R.id.img, R.id.end});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
            }
        });
    }

    // network connection
    private void getPreferences() {

        SharedPreferences sp = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
        String email = sp.getString("email", "");

        String parameter = "?email=" + email;
        Call task = ConnectionTemplate.getConnection("/getPreferenceList", parameter);
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

                    String bodyString = body.string();
                    Log.d(TAG, "body: " + bodyString);
                    preferences = gson.fromJson(bodyString, new TypeToken<List<Preference>>() {
                    }.getType());
                    Log.d(TAG, "meeting: " + preferences);
                }
            }
        });
    }
    // when come back to this class
    @Override
    protected void onResume() {
        super.onResume();
        getPreferences();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();

                updateListView();
            }
            //waiting seconds
        }, 5000);
    }
    //upper right plus icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // when click upper right plus icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this, TimeSlotAdd.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

