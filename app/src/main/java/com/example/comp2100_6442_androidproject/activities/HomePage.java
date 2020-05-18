package com.example.comp2100_6442_androidproject.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.Gps;
import com.example.comp2100_6442_androidproject.domain.ScheduleShow;
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

public class HomePage extends AppCompatActivity {
    private static final String TAG = "HomePage:";
    String email = "";

    List<Gps> gpsList;
    List<String> groupNames;
    List<String> groupDescriptions;

    List<ScheduleShow> scheduleList;
    List<String> scheduleNames;
    List<String> scheduleDescriptions;

    Gson gson = new Gson();
    private boolean isSecondTime = false;

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navigation_groups:
                    updateHomePage();
                    break;
                case R.id.navigation_schedule:
                    updateSchedulePage();
                    break;
                case R.id.navigation_settings:

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LogInPage.class);

        groupNames = new ArrayList<>();
        groupDescriptions = new ArrayList<>();
        scheduleDescriptions = new ArrayList<>();
        scheduleNames = new ArrayList<>();
        startActivity(intent);
        setContentView(R.layout.activity_home_page);

        ImageView navigationGro = (ImageView) findViewById(R.id.navigation_groups);
        ImageView navigationSch = (ImageView) findViewById(R.id.navigation_schedule);
        ImageView navigationSet = (ImageView) findViewById(R.id.navigation_settings);
        navigationGro.setOnClickListener(l);
        navigationSch.setOnClickListener(l);
        navigationSet.setOnClickListener(l);

        updateHomePage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHomePage();
    }

    private void updateHomePage() {

        if (isSecondTime) {
            getGroups();
            ListView listView = (ListView) findViewById(R.id.listview);
            List<Map<String, Object>> listItems = new ArrayList<>();

            for (int i = 0; i < groupNames.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("icon", R.mipmap.groups_img);
                map.put("name", groupNames.get(i));
                map.put("description", groupDescriptions.get(i));
                listItems.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(this, listItems,
                    R.layout.item, new String[]{"name", "icon", "description"},
                    new int[]{R.id.name, R.id.img, R.id.info});
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                    Toast.makeText(HomePage.this, map.get("name").toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            isSecondTime = true;
        }
    }

    private void updateSchedulePage() {
        getSchedules();
        ListView listView = (ListView) findViewById(R.id.listview);
        List<Map<String, Object>> listItems = new ArrayList<>();

        for (int i = 0; i < scheduleNames.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", R.mipmap.meetings_img);
            map.put("name", scheduleNames.get(i));
            map.put("description", scheduleDescriptions.get(i));
            listItems.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, listItems,
                R.layout.item, new String[]{"name", "icon", "description"},
                new int[]{R.id.name, R.id.img, R.id.info});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                Toast.makeText(HomePage.this, map.get("name").toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getGroups() {
        SharedPreferences sp = getSharedPreferences("emailDataBase", Context.MODE_PRIVATE);
        email = sp.getString("email", "");
        String parameter = "?email=" + this.email;
        Call task = ConnectionTemplate.getConnection("/userGroups", parameter);
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
                    gpsList = gson.fromJson(bodyString, new TypeToken<List<Gps>>() {
                    }.getType());
                    Log.d(TAG, "gpsList: " + gpsList);
                    groupNames.clear();
                    groupDescriptions.clear();
                    for (Gps gps : gpsList) {
                        groupNames.add(gps.getName());
                        groupDescriptions.add(gps.getDescription());
                    }
                }
            }
        });
    }

    private void getSchedules() {
        SharedPreferences sp = getSharedPreferences("emailDataBase", Context.MODE_PRIVATE);
        email = sp.getString("email", "");
        String parameter = "?email=" + this.email;
        Call task = ConnectionTemplate.getConnection("/userSchedule", parameter);
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
                    scheduleList = gson.fromJson(bodyString, new TypeToken<List<ScheduleShow>>() {
                    }.getType());
                    Log.d(TAG, "gpsList: " + scheduleList);
                    scheduleNames.clear();
                    scheduleDescriptions.clear();
                    for (ScheduleShow scheduleShow : scheduleList) {
                        scheduleNames.add(scheduleShow.getName());
                        scheduleDescriptions.add(scheduleShow.getInfo());
                    }
                }
            }
        });
    }

}
