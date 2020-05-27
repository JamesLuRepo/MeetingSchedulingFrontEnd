package com.example.comp2100_6442_androidproject.activities;


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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.Gps;
import com.example.comp2100_6442_androidproject.domain.Meeting;
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
    List<String> gpsId;

    List<ScheduleShow> scheduleList;
    List<String> scheduleNames;
    List<String> scheduleDescriptions;
    List<String> scheduleMid;

    Gson gson = new Gson();
    private boolean isSaveSuccessfully = false;
    private  boolean isFirstJump=true;

    //when click the bottom button
    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ProgressDialog progressDialog = new ProgressDialog(HomePage.this);

            switch (v.getId()) {
                case R.id.navigation_groups:
                    getGroups();

                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {// delay 5 millis and then run this
                            progressDialog.dismiss();
                            updateHomePage();
                        }
                        //waiting seconds
                    }, 2000);


                    break;
                case R.id.navigation_schedule:
                    getSchedules();
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {// delay 5 millis and then run this
                            progressDialog.dismiss();
                            updateSchedulePage();
                        }
                        //waiting seconds
                    }, 2000);
                    break;
                case R.id.navigation_settings:
                    updateSettingPage();
                    break;
            }
        }
    };

    // upper right on the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);

    }

    // when choose add groups
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_group) {
            Intent intent = new Intent(this, GroupAdd.class);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == R.id.one_key_schedule){
            Intent intent = new Intent(this, OneKeySchedule.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //initialize the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //   actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        groupNames = new ArrayList<>();
        groupDescriptions = new ArrayList<>();
        gpsId = new ArrayList<>();
        scheduleDescriptions = new ArrayList<>();
        scheduleNames = new ArrayList<>();
        scheduleMid = new ArrayList<>();

        setContentView(R.layout.activity_home_page);
        jumpToLogInPage();

        ImageView navigationGro = (ImageView) findViewById(R.id.navigation_groups);
        ImageView navigationSch = (ImageView) findViewById(R.id.navigation_schedule);
        ImageView navigationSet = (ImageView) findViewById(R.id.navigation_settings);
        navigationGro.setOnClickListener(l);
        navigationSch.setOnClickListener(l);
        navigationSet.setOnClickListener(l);

        getGroups();
        final ProgressDialog progressDialog = new ProgressDialog(HomePage.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();
                updateHomePage();
            }
            //waiting seconds
        }, 2000);

    }
    //only open login page once
    private void jumpToLogInPage(){
        if (isFirstJump){
            Intent intent = new Intent(this, LogInPage.class);
            startActivity(intent);
            isFirstJump=false;
        }
    }

    // when come back to home page
    @Override
    protected void onResume() {
        super.onResume();

        getGroups();
        final ProgressDialog progressDialog = new ProgressDialog(HomePage.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();
                updateHomePage();
            }
            //waiting seconds
        }, 2000);

    }

    //actually this is meeting page, update the viewList
    private void updateHomePage() {
        ListView listView = (ListView) findViewById(R.id.listview);
        List<Map<String, Object>> listItems = new ArrayList<>();

        for (int i = 0; i < groupNames.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", R.mipmap.groups_img);
            Log.d(TAG, "updateHomePage: " + gpsId.get(i));
            map.put("name", "G" + gpsId.get(i) + ":" + groupNames.get(i));
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
                Toast.makeText(HomePage.this, map.get("name").toString().split(":")[1], Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = getSharedPreferences("localDataBase", MODE_PRIVATE).edit();
                //When click one group, the group name and the group description will be saved into the lacal database
                editor.putString("gpsName", map.get("name").toString().split(":")[1]);
                editor.putString("gpsDescription", map.get("description").toString());
                editor.putString("gpsId", map.get("name").toString().split(":")[0].substring(1));
                editor.commit();

                Intent intent = new Intent(HomePage.this, GroupInfo.class);
                startActivity(intent);
            }
        });

    }
    //update the schedule viewList
    private void updateSchedulePage() {

        ListView listView = (ListView) findViewById(R.id.listview);
        List<Map<String, Object>> listItems = new ArrayList<>();

        for (int i = 0; i < scheduleNames.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", R.mipmap.meetings_img);
            map.put("name", "M" + scheduleMid.get(i) + ":" + scheduleNames.get(i));
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
                Toast.makeText(HomePage.this, map.get("name").toString().split(":")[1], Toast.LENGTH_SHORT).show();
                isSaveSuccessfully = false;
                getMeetingInformation(map.get("name").toString().split(":")[0].substring(1));
                // the progressDialog to wait for the respond
                final ProgressDialog progressDialog = new ProgressDialog(HomePage.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {// delay 3.5 millis and then run this
                        progressDialog.dismiss();
                        if (isSaveSuccessfully) {
                            Intent intent = new Intent(HomePage.this, MeetingInfo.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(HomePage.this, "click again to get network response", Toast.LENGTH_SHORT).show();
                        }


                    }
                    //waiting seconds
                }, 6000);


            }
        });
    }
    //jump to the setting page
    private void updateSettingPage() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

    }

    //network connection
    private void getGroups() {
        // get data from the local database
        SharedPreferences sp = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
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
                    gpsId.clear();
                    for (Gps gps : gpsList) {
                        groupNames.add(gps.getName());
                        groupDescriptions.add(gps.getDescription());
                        gpsId.add(gps.getGid() + "");
                    }
                }
            }
        });
    }
    //network connection
    private void getSchedules() {
        SharedPreferences sp = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
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
                    scheduleMid.clear();
                    for (ScheduleShow scheduleShow : scheduleList) {
                        scheduleNames.add(scheduleShow.getName());
                        scheduleDescriptions.add(scheduleShow.getInfo());
                        scheduleMid.add(scheduleShow.getMid() + "");

                    }
                }
            }
        });
    }
    //network connection prepare for opening the meetings
    private void getMeetingInformation(String mid) {
        Log.d(TAG, "getMeetingInformation: mid:" + mid);
        String parameter = "?mid=" + mid;
        Call task = ConnectionTemplate.getConnection("/getMeetingInformation", parameter);
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
                    Meeting meeting = gson.fromJson(bodyString, Meeting.class);
                    Log.d(TAG, "meeting: " + meeting);


                    SharedPreferences.Editor editor = getSharedPreferences("localDataBase", MODE_PRIVATE).edit();
                    //When click one schedule, the the meeting information will be saved into the local database
                    editor.putString("meetingName", meeting.getName());
                    editor.putString("meetingNotes", meeting.getNotes());
                    editor.putString("meetingHoldTime", meeting.getHoldTime());
                    editor.putString("meetingTimeLength", meeting.getTimeLength() + "");
                    editor.putString("meetingLocation", meeting.getLocation());
                    editor.putString("meetingScheduling_ddl", meeting.getScheduling_ddl());
                    editor.putString("meetingMId", meeting.getMid() + "");
                    editor.putString("meetingGId", meeting.getGpsGid() + "");
                    editor.commit();
                    isSaveSuccessfully = true;

                }
            }
        });
    }

}
